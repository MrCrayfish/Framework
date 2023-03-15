package com.mrcrayfish.framework.platform.network;

import com.mrcrayfish.framework.api.network.MessageDirection;
import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.network.Connection;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Author: MrCrayfish
 */
public class FabricMessageContext extends MessageContext
{
    private final Executor executor;
    private final Connection connection;

    public FabricMessageContext(Executor executor, Connection connection, MessageDirection direction)
    {
        super(direction);
        this.executor = executor;
        this.connection = connection;
    }

    @Override
    public void setHandled(boolean handled)
    {
        // Unused
    }

    @Override
    public CompletableFuture<Void> execute(Runnable runnable)
    {
        this.executor.execute(runnable);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public Connection getNetworkManager()
    {
        return this.connection;
    }
}
