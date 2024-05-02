package com.mrcrayfish.framework.platform.network;

import com.google.common.collect.EnumBiMap;
import com.mrcrayfish.framework.api.network.FrameworkNetwork;
import com.mrcrayfish.framework.api.network.FrameworkNetworkBuilder;
import com.mrcrayfish.framework.api.network.FrameworkResponse;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.network.message.configuration.Acknowledge;
import net.minecraft.Util;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.SimpleChannel;
import org.jetbrains.annotations.Nullable;

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
public class ForgeNetworkBuilder implements FrameworkNetworkBuilder
{
    private static final EnumBiMap<PacketFlow, NetworkDirection> DIRECTION_MAPPER = Util.make(EnumBiMap.create(PacketFlow.class, NetworkDirection.class), map -> {
        map.put(PacketFlow.CLIENTBOUND, NetworkDirection.PLAY_TO_CLIENT);
        map.put(PacketFlow.SERVERBOUND, NetworkDirection.PLAY_TO_SERVER);
    });

    private final ResourceLocation id;
    private final int version;
    private boolean optional = false;
    private final List<BiConsumer<Supplier<RegistryAccess>, SimpleChannel>> playMessages = new ArrayList<>();
    private final List<Consumer<SimpleChannel>> configurationMessages = new ArrayList<>();
    private final List<Function<SimpleChannel, ConfigurationTask>> configurationTasks = new ArrayList<>();

    public ForgeNetworkBuilder(ResourceLocation id, int version)
    {
        this.id = id;
        this.version = version;
    }

    @Override
    public ForgeNetworkBuilder optional()
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
        NetworkDirection direction = DIRECTION_MAPPER.get(flow);
        this.playMessages.add((access, channel) -> channel
            .messageBuilder(messageClass, direction)
            .encoder((msg, buf) -> codec.encode(RegistryFriendlyByteBuf.decorator(access.get()).apply(buf), msg))
            .decoder(buf -> codec.decode(RegistryFriendlyByteBuf.decorator(access.get()).apply(buf)))
            .consumerNetworkThread((msg, ctx) -> {
                PacketFlow receivingFlow = ctx.getConnection().getReceiving();
                MessageContext context = new ForgeMessageContext(ctx, receivingFlow);
                handler.accept(msg, context);
                context.getReply().ifPresent(reply -> channel.reply(reply, ctx));
            }).add());
        return this;
    }

    @Override
    public <T> FrameworkNetworkBuilder registerConfigurationMessage(String name, Class<T> taskClass, StreamCodec<FriendlyByteBuf, T> codec, BiFunction<T, Consumer<Runnable>, FrameworkResponse> handler, Supplier<List<T>> messages)
    {
        return this.registerConfigurationMessage(name, taskClass, codec, handler, messages, null);
    }

    @Override
    public <T> FrameworkNetworkBuilder registerConfigurationMessage(String name, Class<T> taskClass, StreamCodec<FriendlyByteBuf, T> codec, BiFunction<T, Consumer<Runnable>, FrameworkResponse> handler, Supplier<List<T>> messages, @Nullable PacketFlow flow)
    {
        this.registerConfigurationAckMessage();
        this.configurationMessages.add(channel -> channel
            .messageBuilder(taskClass, NetworkDirection.PLAY_TO_CLIENT)
            .encoder((msg, buf) -> codec.encode(buf, msg))
            .decoder(codec::decode)
            .consumerNetworkThread((msg, ctx) -> {
                PacketFlow receivingFlow = ctx.getConnection().getReceiving();
                MessageContext context = new ForgeMessageContext(ctx, receivingFlow);
                FrameworkResponse response = handler.apply(msg, context::execute);
                if(response.isError()) {
                    context.disconnect(Component.literal("Connection closed - " + response.message()));
                    return;
                }
                context.setHandled(true);
                channel.reply(new Acknowledge(), ctx);
            }).add());
        ResourceLocation taskId = FrameworkNetworkBuilder.createMessageId(this.id, name);
        ConfigurationTask.Type type = new ConfigurationTask.Type(taskId.toString());
        this.configurationTasks.add(channel -> new ForgeConfigurationTask<>(channel, type, messages));
        return this;
    }

    private void registerConfigurationAckMessage()
    {
        if(this.configurationMessages.isEmpty())
        {
            this.configurationMessages.add(channel -> channel
                .messageBuilder(Acknowledge.class, NetworkDirection.PLAY_TO_SERVER)
                .encoder((msg, buf) -> Acknowledge.STREAM_CODEC.encode(buf, msg))
                .decoder(Acknowledge.STREAM_CODEC::decode)
                .consumerNetworkThread((msg, ctx) -> {
                    PacketFlow receivingFlow = ctx.getConnection().getReceiving();
                    MessageContext context = new ForgeMessageContext(ctx, receivingFlow);
                    Acknowledge.handle(msg, context);
                }).add());
        }
    }

    public FrameworkNetwork build()
    {
        ChannelBuilder builder = ChannelBuilder.named(this.id).networkProtocolVersion(this.version);
        if(this.optional) builder.optional();
        return new ForgeNetwork(builder, this.playMessages, this.configurationMessages, this.configurationTasks);
    }
}
