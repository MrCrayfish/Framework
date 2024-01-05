package com.mrcrayfish.framework.platform.network;

import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.MessageDirection;
import net.minecraft.network.Connection;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.util.concurrent.CompletableFuture;

/**
 * Author: MrCrayfish
 */
public class ForgeMessageContext extends MessageContext
{
    private final CustomPayloadEvent.Context context;

    public ForgeMessageContext(CustomPayloadEvent.Context context, MessageDirection direciton)
    {
        super(direciton, context.getSender());
        this.context = context;
    }

    public CustomPayloadEvent.Context getNetworkContext()
    {
        return this.context;
    }

    @Override
    public CompletableFuture<Void> execute(Runnable runnable)
    {
        return this.context.enqueueWork(runnable);
    }

    @Override
    public Connection getNetworkManager()
    {
        return this.context.getConnection();
    }

    @Override
    public void setHandled(boolean handled)
    {
        this.context.setPacketHandled(handled);
    }
}
