package com.mrcrayfish.framework.platform.network;

import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketFlow;
import net.neoforged.neoforge.network.handling.ConfigurationPayloadContext;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.function.Consumer;

/**
 * Author: MrCrayfish
 */
public class NeoForgeMessageContext extends MessageContext
{
    private final Consumer<Runnable> executor;
    private final Consumer<Component> disconnector;

    public NeoForgeMessageContext(PlayPayloadContext context, PacketFlow flow)
    {
        super(flow, context.player().orElse(null));
        this.executor = context.workHandler()::execute;
        this.disconnector = context.packetHandler()::disconnect;
    }

    public NeoForgeMessageContext(ConfigurationPayloadContext context, PacketFlow flow)
    {
        super(flow, context.player().orElse(null));
        this.executor = context.workHandler()::execute;
        this.disconnector = context.packetHandler()::disconnect;
    }

    @Override
    public void execute(Runnable runnable)
    {
        this.executor.accept(runnable);
    }

    @Override
    public void disconnect(Component reason)
    {
        this.disconnector.accept(reason);
    }

    @Override
    public void setHandled(boolean handled)
    {
        // TODO what this do on neoforge
    }
}
