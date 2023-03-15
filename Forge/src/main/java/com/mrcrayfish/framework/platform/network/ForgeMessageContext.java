package com.mrcrayfish.framework.platform.network;

import com.mrcrayfish.framework.api.network.MessageDirection;
import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.network.Connection;
import net.minecraftforge.network.NetworkEvent;

import java.util.concurrent.CompletableFuture;

/**
 * Author: MrCrayfish
 */
public class ForgeMessageContext extends MessageContext
{
    private final NetworkEvent.Context context;

    public ForgeMessageContext(NetworkEvent.Context context, MessageDirection direciton)
    {
        super(direciton);
        this.context = context;
    }

    public NetworkEvent.Context getNetworkContext()
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
        return this.context.getNetworkManager();
    }

    @Override
    public void setHandled(boolean handled)
    {
        this.context.setPacketHandled(handled);
    }
}
