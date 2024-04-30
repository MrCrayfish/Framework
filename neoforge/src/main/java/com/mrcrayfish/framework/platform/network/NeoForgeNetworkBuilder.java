package com.mrcrayfish.framework.platform.network;

import com.mrcrayfish.framework.api.network.FrameworkNetwork;
import com.mrcrayfish.framework.api.network.FrameworkNetworkBuilder;
import com.mrcrayfish.framework.api.network.FrameworkResponse;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.network.message.ConfigurationMessage;
import com.mrcrayfish.framework.network.message.FrameworkMessage;
import com.mrcrayfish.framework.network.message.FrameworkPayload;
import com.mrcrayfish.framework.network.message.PlayMessage;
import com.mrcrayfish.framework.network.message.configuration.Acknowledge;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class NeoForgeNetworkBuilder implements FrameworkNetworkBuilder
{
    private final ResourceLocation id;
    private final int version;
    private boolean optional = false;
    private final List<FrameworkMessage<?, RegistryFriendlyByteBuf>> playMessages = new ArrayList<>();
    private final List<BiConsumer<NeoForgeNetwork, PayloadRegistrar>> playPayloads = new ArrayList<>();
    private final List<FrameworkMessage<?, FriendlyByteBuf>> configurationMessages = new ArrayList<>();
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
    public <T> FrameworkNetworkBuilder registerPlayMessage(String name, Class<T> messageClass, StreamCodec<RegistryFriendlyByteBuf, T> codec, BiConsumer<T, MessageContext> handler)
    {
        return this.registerPlayMessage(name, messageClass, codec, handler, null);
    }

    @Override
    public <T> FrameworkNetworkBuilder registerPlayMessage(String name, Class<T> messageClass, StreamCodec<RegistryFriendlyByteBuf, T> codec, BiConsumer<T, MessageContext> handler, @Nullable PacketFlow flow)
    {
        ResourceLocation payloadId = FrameworkNetworkBuilder.createMessageId(this.id, name);
        CustomPacketPayload.Type<FrameworkPayload<T>> payloadType = new CustomPacketPayload.Type<>(payloadId);
        StreamCodec<RegistryFriendlyByteBuf, FrameworkPayload<T>> payloadCodec = FrameworkPayload.codec(payloadType, codec);
        FrameworkMessage<T, RegistryFriendlyByteBuf> message = new PlayMessage<>(payloadType, messageClass, payloadCodec, handler, flow);
        this.playMessages.add(message);
        this.playPayloads.add((network, registrar) -> {
            this.<FrameworkPayload<T>>getPlayFunction(registrar, message.flow()).apply(message.type(), message.codec(), (payload, ctx) -> {
                MessageContext context = new NeoForgeMessageContext(ctx, ctx.flow(), ctx.player());
                message.handler().accept(payload.msg(), context);
                context.getReply().ifPresent(msg -> ctx.reply(network.encode(msg)));
            });
        });
        return this;
    }

    @Override
    public <T> FrameworkNetworkBuilder registerConfigurationMessage(String name, Class<T> taskClass, StreamCodec<FriendlyByteBuf, T> codec, BiFunction<T, Consumer<Runnable>, FrameworkResponse> handler, Supplier<List<T>> messages)
    {
        return this.registerConfigurationMessage(name, taskClass, codec, handler, messages, PacketFlow.CLIENTBOUND);
    }

    @Override
    public <T> FrameworkNetworkBuilder registerConfigurationMessage(String name, Class<T> taskClass, StreamCodec<FriendlyByteBuf, T> codec, BiFunction<T, Consumer<Runnable>, FrameworkResponse> handler, Supplier<List<T>> messages, @Nullable PacketFlow flow)
    {
        this.registerConfigurationAckMessage();
        ResourceLocation payloadId = FrameworkNetworkBuilder.createMessageId(this.id, name);
        CustomPacketPayload.Type<FrameworkPayload<T>> payloadType = new CustomPacketPayload.Type<>(payloadId);
        StreamCodec<FriendlyByteBuf, FrameworkPayload<T>> payloadCodec = FrameworkPayload.codec(payloadType, codec);
        FrameworkMessage<T, FriendlyByteBuf> message = new ConfigurationMessage<>(payloadType, taskClass, payloadCodec, handler, flow);
        this.configurationMessages.add(message);
        this.configurationPayloads.add(this.createConfigurationPayloadConsumer(message));
        ConfigurationTask.Type taskType = new ConfigurationTask.Type(message.type().id().toString());
        this.configurationTasks.add((network, listener) -> {
            return new NeoForgeConfigurationTask<>(network, listener, taskType, messages);
        });
        return this;
    }

    private void registerConfigurationAckMessage()
    {
        if(this.configurationMessages.isEmpty())
        {
            ResourceLocation payloadId = FrameworkNetworkBuilder.createMessageId(this.id, "ack");
            CustomPacketPayload.Type<FrameworkPayload<Acknowledge>> payloadType = new CustomPacketPayload.Type<>(payloadId);
            StreamCodec<FriendlyByteBuf, FrameworkPayload<Acknowledge>> payloadCodec = FrameworkPayload.codec(payloadType, Acknowledge.STREAM_CODEC);
            FrameworkMessage<Acknowledge, FriendlyByteBuf> message = new FrameworkMessage<>(payloadType, Acknowledge.class, payloadCodec, Acknowledge::handle, PacketFlow.SERVERBOUND);
            this.configurationMessages.add(message);
            this.configurationPayloads.add(this.createConfigurationPayloadConsumer(message));
        }
    }

    private <T> BiConsumer<NeoForgeNetwork, PayloadRegistrar> createConfigurationPayloadConsumer(FrameworkMessage<T, FriendlyByteBuf> message)
    {
        return (network, registrar) -> {
            this.<FrameworkPayload<T>>getConfigurationFunction(registrar, message.flow()).apply(message.type(), message.codec(), (payload, ctx) -> {
                MessageContext context = new NeoForgeMessageContext(ctx, ctx.flow(), null);
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
