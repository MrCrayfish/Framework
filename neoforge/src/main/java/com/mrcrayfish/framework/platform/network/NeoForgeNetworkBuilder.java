package com.mrcrayfish.framework.platform.network;

import com.mrcrayfish.framework.api.network.ConfigurationMessageContext;
import com.mrcrayfish.framework.api.network.FrameworkNetwork;
import com.mrcrayfish.framework.api.network.FrameworkNetworkBuilder;
import com.mrcrayfish.framework.api.network.PlayMessageContext;
import com.mrcrayfish.framework.network.message.ConfigurationMessage;
import com.mrcrayfish.framework.network.message.FrameworkPayload;
import com.mrcrayfish.framework.network.message.PlayMessage;
import com.mrcrayfish.framework.network.message.configuration.FinishedConfigurationTask;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.ConfigurationTask;
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.apache.commons.lang3.function.TriFunction;

import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class NeoForgeNetworkBuilder implements FrameworkNetworkBuilder
{
    private final ResourceLocation id;
    private final int version;
    private boolean optional = false;
    private final List<PlayMessage<?>> playMessages = new ArrayList<>();
    private final List<BiConsumer<NeoForgeNetwork, PayloadRegistrar>> playPayloads = new ArrayList<>();
    private final List<ConfigurationMessage<?>> configurationMessages = new ArrayList<>();
    private final List<BiConsumer<NeoForgeNetwork, PayloadRegistrar>> configurationPayloads = new ArrayList<>();
    private final List<BiFunction<NeoForgeNetwork, ServerConfigurationPacketListener, ICustomConfigurationTask>> configurationTasks = new ArrayList<>();

    public NeoForgeNetworkBuilder(ResourceLocation id, int version)
    {
        this.id = id;
        this.version = version;
    }

    @Override
    public NeoForgeNetworkBuilder optional()
    {
        this.optional = true;
        return this;
    }

    @Override
    public <T> FrameworkNetworkBuilder registerPlayMessage(String name, Class<T> messageClass, StreamCodec<RegistryFriendlyByteBuf, T> codec, BiConsumer<T, PlayMessageContext> handler)
    {
        return this.registerPlayMessage(name, messageClass, codec, handler, null);
    }

    @Override
    public <T> FrameworkNetworkBuilder registerPlayMessage(String name, Class<T> messageClass, StreamCodec<RegistryFriendlyByteBuf, T> codec, BiConsumer<T, PlayMessageContext> handler, @Nullable PacketFlow flow)
    {
        ResourceLocation payloadId = FrameworkNetworkBuilder.createMessageId(this.id, name);
        CustomPacketPayload.Type<FrameworkPayload<T>> payloadType = new CustomPacketPayload.Type<>(payloadId);
        StreamCodec<RegistryFriendlyByteBuf, FrameworkPayload<T>> payloadCodec = FrameworkPayload.codec(payloadType, codec);
        PlayMessage<T> message = new PlayMessage<>(payloadType, messageClass, payloadCodec, handler, flow);
        this.playMessages.add(message);
        this.playPayloads.add((network, registrar) -> {
            this.<FrameworkPayload<T>>getPlayFunction(registrar, message.flow()).apply(message.type(), message.codec(), (payload, ctx) -> {
                PlayMessageContext context = new PlayMessageContext(ctx.flow(), ctx::enqueueWork, ctx::disconnect, b -> {}, ctx.player());
                message.handler().accept(payload.msg(), context);
                context.getReply().ifPresent(msg -> ctx.reply(network.encode(msg)));
            });
        });
        return this;
    }

    @Override
    public <T> FrameworkNetworkBuilder registerConfigurationMessage(String name, Class<T> taskClass, StreamCodec<FriendlyByteBuf, T> codec, BiConsumer<T, ConfigurationMessageContext> handler, Supplier<List<T>> messages)
    {
        return this.registerConfigurationMessage(name, taskClass, codec, handler, messages, PacketFlow.CLIENTBOUND);
    }

    @Override
    public <T> FrameworkNetworkBuilder registerConfigurationMessage(String name, Class<T> taskClass, StreamCodec<FriendlyByteBuf, T> codec, BiConsumer<T, ConfigurationMessageContext> handler, Supplier<List<T>> messages, @Nullable PacketFlow flow)
    {
        return this.registerConfigurationMessage(name, taskClass, codec, handler, messages, flow, true);
    }

    @Override
    public <T> FrameworkNetworkBuilder registerConfigurationMessage(String name, Class<T> taskClass, StreamCodec<FriendlyByteBuf, T> codec, BiConsumer<T, ConfigurationMessageContext> handler, Supplier<List<T>> messages, @Nullable PacketFlow flow, boolean completeImmediately)
    {
        this.registerConfigurationAckMessage();
        ResourceLocation payloadId = FrameworkNetworkBuilder.createMessageId(this.id, name);
        CustomPacketPayload.Type<FrameworkPayload<T>> payloadType = new CustomPacketPayload.Type<>(payloadId);
        StreamCodec<FriendlyByteBuf, FrameworkPayload<T>> payloadCodec = FrameworkPayload.codec(payloadType, codec);
        ConfigurationMessage<T> message = new ConfigurationMessage<>(payloadType, taskClass, payloadCodec, handler, flow);
        ConfigurationTask.Type taskType = new ConfigurationTask.Type(message.type().id());
        this.configurationMessages.add(message);
        this.configurationPayloads.add(this.createConfigurationPayloadConsumer(message));
        this.configurationTasks.add((network, listener) -> {
            return new NeoForgeConfigurationTask<>(network, listener, taskType, messages, completeImmediately);
        });
        return this;
    }

    private void registerConfigurationAckMessage()
    {
        if(this.configurationMessages.isEmpty())
        {
            ResourceLocation payloadId = FrameworkNetworkBuilder.createMessageId(this.id, "ack");
            CustomPacketPayload.Type<FrameworkPayload<FinishedConfigurationTask>> payloadType = new CustomPacketPayload.Type<>(payloadId);
            StreamCodec<FriendlyByteBuf, FrameworkPayload<FinishedConfigurationTask>> payloadCodec = FrameworkPayload.codec(payloadType, FinishedConfigurationTask.STREAM_CODEC);
            ConfigurationMessage<FinishedConfigurationTask> message = new ConfigurationMessage<>(payloadType, FinishedConfigurationTask.class, payloadCodec, FinishedConfigurationTask::handle, null);
            this.configurationMessages.add(message);
            this.configurationPayloads.add(this.createConfigurationPayloadConsumer(message));
        }
    }

    private <T> BiConsumer<NeoForgeNetwork, PayloadRegistrar> createConfigurationPayloadConsumer(ConfigurationMessage<T> message)
    {
        return (network, registrar) -> {
            this.<FrameworkPayload<T>>getConfigurationFunction(registrar, message.flow()).apply(message.type(), message.codec(), (payload, ctx) -> {
                ConfigurationMessageContext context = new ConfigurationMessageContext(ctx.flow(), ctx::enqueueWork, ctx::disconnect, b -> {}, id -> {
                    ctx.finishCurrentTask(new ConfigurationTask.Type(id));
                });
                message.handler().accept(payload.msg(), context);
                context.getReply().ifPresent(msg -> ctx.reply(network.encode(msg)));
            });
        };
    }

    private <T extends CustomPacketPayload> TriFunction<CustomPacketPayload.Type<T>, StreamCodec<RegistryFriendlyByteBuf, T>, IPayloadHandler<T>, PayloadRegistrar> getPlayFunction(PayloadRegistrar registrar, @Nullable PacketFlow flow)
    {
        if(flow == PacketFlow.CLIENTBOUND) return registrar::playToClient;
        if(flow == PacketFlow.SERVERBOUND) return registrar::playToServer;
        return registrar::playBidirectional;
    }

    private <T extends CustomPacketPayload> TriFunction<CustomPacketPayload.Type<T>, StreamCodec<FriendlyByteBuf, T>, IPayloadHandler<T>, PayloadRegistrar> getConfigurationFunction(PayloadRegistrar registrar, @Nullable PacketFlow flow)
    {
        if(flow == PacketFlow.CLIENTBOUND) return registrar::configurationToClient;
        if(flow == PacketFlow.SERVERBOUND) return registrar::configurationToServer;
        return registrar::configurationBidirectional;
    }

    public FrameworkNetwork build()
    {
        return new NeoForgeNetwork(this.id, this.version, this.optional, this.playMessages, this.playPayloads, this.configurationMessages, this.configurationPayloads, this.configurationTasks);
    }
}
