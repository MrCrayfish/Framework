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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;

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
public class FabricNetworkBuilder implements FrameworkNetworkBuilder
{
    private final ResourceLocation id;
    private final int version;
    private final List<FrameworkMessage<?>> playMessages = new ArrayList<>();
    private final List<FrameworkMessage<?>> configurationMessages = new ArrayList<>();
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
    public <T> FrameworkNetworkBuilder registerPlayMessage(String name, Class<T> messageClass, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, MessageContext> handler)
    {
        return this.registerPlayMessage(name, messageClass, encoder, decoder, handler, null);
    }

    @Override
    public <T> FrameworkNetworkBuilder registerPlayMessage(String name, Class<T> messageClass, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, MessageContext> handler, @Nullable PacketFlow flow)
    {
        ResourceLocation id = this.id.withPath(name);
        this.playMessages.add(new FrameworkMessage<>(id, messageClass, encoder, decoder, handler, flow));
        return this;
    }

    @Override
    public <T> FrameworkNetworkBuilder registerConfigurationMessage(String name, Class<T> taskClass, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiFunction<T, Consumer<Runnable>, FrameworkResponse> handler, Supplier<List<T>> messages)
    {
        this.registerConfigurationAckMessage();
        ResourceLocation id = this.id.withPath(name);
        this.configurationMessages.add(new ConfigurationMessage<>(id, taskClass, encoder, decoder, handler));
        ConfigurationTask.Type type = new ConfigurationTask.Type(this.id.withPath(name).toString());
        this.configurationTasks.add((network, listener) -> new FabricConfigurationTask<>(network, listener, type, messages));
        return this;
    }

    private void registerConfigurationAckMessage()
    {
        if(this.configurationMessages.isEmpty())
        {
            ResourceLocation id = this.id.withPath("ack");
            this.configurationMessages.add(new FrameworkMessage<>(id, Acknowledge.class, Acknowledge::encode, Acknowledge::decode, Acknowledge::handle, PacketFlow.SERVERBOUND));
        }
    }

    @Override
    public FrameworkNetwork build()
    {
        return new FabricNetwork(this.id, this.version, this.playMessages, this.configurationMessages, this.configurationTasks);
    }
}
