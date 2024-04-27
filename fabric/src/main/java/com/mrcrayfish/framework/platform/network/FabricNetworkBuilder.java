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
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
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
public class FabricNetworkBuilder implements FrameworkNetworkBuilder
{
    private final ResourceLocation id;
    private final int version;
    private final List<PlayMessage<?>> playMessages = new ArrayList<>();
    private final List<FrameworkMessage<?, FriendlyByteBuf>> configurationMessages = new ArrayList<>();
    private final List<BiFunction<FabricNetwork, ServerConfigurationPacketListenerImpl, ConfigurationTask>> configurationTasks = new ArrayList<>();

    public FabricNetworkBuilder(ResourceLocation id, int version)
    {
        this.id = id;
        this.version = version;
    }

    @Override
    public FrameworkNetworkBuilder optional()
    {
        return this;
    }

    @Override
    public <T> FrameworkNetworkBuilder registerPlayMessage(String name, Class<T> messageClass, StreamCodec<RegistryFriendlyByteBuf, T> codec, BiConsumer<T, MessageContext> handler)
    {
        return this.registerPlayMessage(name, messageClass, codec, handler, null);
    }

    @Override
    public <T> FrameworkNetworkBuilder registerPlayMessage(String name, Class<T> messageClass, StreamCodec<RegistryFriendlyByteBuf, T> codec, BiConsumer<T, MessageContext> handler, @org.jetbrains.annotations.Nullable PacketFlow flow)
    {
        ResourceLocation payloadId = FrameworkNetworkBuilder.createMessageId(this.id, name);
        CustomPacketPayload.Type<FrameworkPayload<T>> payloadType = new CustomPacketPayload.Type<>(payloadId);
        StreamCodec<RegistryFriendlyByteBuf, FrameworkPayload<T>> payloadCodec = FrameworkPayload.codec(payloadType, codec);
        this.playMessages.add(new PlayMessage<>(payloadType, messageClass, payloadCodec, handler, flow));
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
        ResourceLocation payloadId = FrameworkNetworkBuilder.createMessageId(this.id, name);
        CustomPacketPayload.Type<FrameworkPayload<T>> payloadType = new CustomPacketPayload.Type<>(payloadId);
        StreamCodec<FriendlyByteBuf, FrameworkPayload<T>> payloadCodec = FrameworkPayload.codec(payloadType, codec);
        this.configurationMessages.add(new ConfigurationMessage<>(payloadType, taskClass, payloadCodec, handler, flow));
        ConfigurationTask.Type type = new ConfigurationTask.Type(payloadId.toString());
        this.configurationTasks.add((network, listener) -> new FabricConfigurationTask<>(payloadId, network, listener, type, messages));
        return this;
    }

    private void registerConfigurationAckMessage()
    {
        if(this.configurationMessages.isEmpty())
        {
            ResourceLocation payloadId = FrameworkNetworkBuilder.createMessageId(this.id, "ack");
            CustomPacketPayload.Type<FrameworkPayload<Acknowledge>> payloadType = new CustomPacketPayload.Type<>(payloadId);
            StreamCodec<FriendlyByteBuf, FrameworkPayload<Acknowledge>> payloadCodec = FrameworkPayload.codec(payloadType, Acknowledge.STREAM_CODEC);
            this.configurationMessages.add(new FrameworkMessage<>(payloadType, Acknowledge.class, payloadCodec, Acknowledge::handle, PacketFlow.SERVERBOUND));
        }
    }

    @Override
    public FrameworkNetwork build()
    {
        this.registerConfigurationMessage("ping", FabricNetwork.Ping.class, FabricNetwork.Ping.STREAM_CODEC, FabricNetwork.Ping::handle, () -> List.of(new FabricNetwork.Ping()), PacketFlow.CLIENTBOUND);
        return new FabricNetwork(this.id, this.version, this.playMessages, this.configurationMessages, this.configurationTasks);
    }
}
