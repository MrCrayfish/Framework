package com.mrcrayfish.framework.platform.network;

import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketFlow;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.function.Consumer;

/**
 * Author: MrCrayfish
 */
public class NeoForgeMessageContext extends MessageContext
{
    private final Consumer<Runnable> executor;
    private final Consumer<Component> disconnector;

    public NeoForgeMessageContext(IPayloadContext context, PacketFlow flow)
    {
        super(flow, context.player());
        this.executor = context::enqueueWork;
        this.disconnector = context.connection()::disconnect;
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
