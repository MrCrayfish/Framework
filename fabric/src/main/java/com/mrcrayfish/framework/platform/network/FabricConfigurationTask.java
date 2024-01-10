package com.mrcrayfish.framework.platform.network;

import com.mrcrayfish.framework.Constants;
import com.mrcrayfish.framework.network.message.ConfigurationMessage;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class FabricConfigurationTask<T> implements ConfigurationTask
{
    private final FabricNetwork network;
    private final ServerConfigurationPacketListenerImpl listener;
    private final Type type;
    private final Supplier<List<T>> messages;

    public FabricConfigurationTask(FabricNetwork network, ServerConfigurationPacketListenerImpl listener, Type type, Supplier<List<T>> messages)
    {
        this.network = network;
        this.listener = listener;
        this.type = type;
        this.messages = messages;
    }

    @Override
    public void start(Consumer<Packet<?>> consumer)
    {
        Constants.LOG.debug(ConfigurationMessage.MARKER, "Sending configuration task '%s'".formatted(this.type.id()));
        this.messages.get().forEach(msg -> {
            consumer.accept(ServerPlayNetworking.createS2CPacket(this.network.id, this.network.encode(msg)));
        });
        this.listener.completeTask(this.type);
    }

    @Override
    public Type type()
    {
        return this.type;
    }
}
