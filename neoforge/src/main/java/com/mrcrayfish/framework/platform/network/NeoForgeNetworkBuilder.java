package com.mrcrayfish.framework.platform.network;

import com.mrcrayfish.framework.api.network.FrameworkNetwork;
import com.mrcrayfish.framework.api.network.FrameworkNetworkBuilder;
import com.mrcrayfish.framework.api.network.FrameworkResponse;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.network.message.ConfigurationMessage;
import com.mrcrayfish.framework.network.message.FrameworkMessage;
import com.mrcrayfish.framework.network.message.configuration.Acknowledge;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.ConfigurationTask;
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class NeoForgeNetworkBuilder implements FrameworkNetworkBuilder
{
    private final ResourceLocation id;
    private final int version;
    private boolean optional = false;
    private final List<FrameworkMessage<?>> playMessages = new ArrayList<>();
    private final List<BiConsumer<NeoForgeNetwork, IPayloadRegistrar>> playPayloads = new ArrayList<>();
    private final List<FrameworkMessage<?>> configurationMessages = new ArrayList<>();
    private final List<BiConsumer<NeoForgeNetwork, IPayloadRegistrar>> configurationPayloads = new ArrayList<>();
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
    public <T> FrameworkNetworkBuilder registerPlayMessage(String name, Class<T> messageClass, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, MessageContext> handler)
    {
        return this.registerPlayMessage(name, messageClass, encoder, decoder, handler, null);
    }

    @Override
    public <T> FrameworkNetworkBuilder registerPlayMessage(String name, Class<T> messageClass, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, MessageContext> handler, @Nullable PacketFlow flow)
    {
        ResourceLocation id = this.id.withPath(name);
        FrameworkMessage<T> message = new FrameworkMessage<>(id, messageClass, encoder, decoder, handler, flow);
        this.playMessages.add(message);
        this.playPayloads.add((network, registrar) -> {
            registrar.play(id, message::readPayload, (payload, ctx) -> {
                MessageContext context = new NeoForgeMessageContext(ctx, ctx.flow());
                message.handler().accept(payload.msg(), context);
                context.getReply().ifPresent(msg -> ctx.replyHandler().send(network.encode(msg)));
            });
        });
        return this;
    }

    @Override
    public <T> FrameworkNetworkBuilder registerConfigurationMessage(String name, Class<T> taskClass, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiFunction<T, Consumer<Runnable>, FrameworkResponse> handler, Supplier<List<T>> messages)
    {
        this.registerConfigurationAckMessage();
        ResourceLocation id = this.id.withPath(name);
        FrameworkMessage<T> message = new ConfigurationMessage<>(id, taskClass, encoder, decoder, handler);
        this.configurationMessages.add(message);
        this.configurationPayloads.add(this.createConfigurationPayloadConsumer(message));
        ConfigurationTask.Type type = new ConfigurationTask.Type(id.toString());
        this.configurationTasks.add((network, listener) -> {
            return new NeoForgeConfigurationTask<>(network, listener, type, messages);
        });
        return this;
    }

    private void registerConfigurationAckMessage()
    {
        if(this.configurationMessages.isEmpty())
        {
            ResourceLocation id = this.id.withPath("ack");
            FrameworkMessage<Acknowledge> message = new FrameworkMessage<>(id, Acknowledge.class, Acknowledge::encode, Acknowledge::decode, Acknowledge::handle, PacketFlow.SERVERBOUND);
            this.configurationMessages.add(message);
            this.configurationPayloads.add(this.createConfigurationPayloadConsumer(message));
        }
    }

    private <T> BiConsumer<NeoForgeNetwork, IPayloadRegistrar> createConfigurationPayloadConsumer(FrameworkMessage<T> message)
    {
        return (network, registrar) -> registrar.configuration(message.id(), message::readPayload, (payload, ctx) -> {
            MessageContext context = new NeoForgeMessageContext(ctx, ctx.flow());
            message.handler().accept(payload.msg(), context);
            context.getReply().ifPresent(msg -> ctx.replyHandler().send(network.encode(msg)));
        });
    }

    public FrameworkNetwork build()
    {
        return new NeoForgeNetwork(this.id, this.version, this.optional, this.playMessages, this.playPayloads, this.configurationMessages, this.configurationPayloads, this.configurationTasks);
    }
}
