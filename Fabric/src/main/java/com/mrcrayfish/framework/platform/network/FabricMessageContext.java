package com.mrcrayfish.framework.platform.network;

import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.MessageDirection;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Author: MrCrayfish
 */
public class FabricMessageContext extends MessageContext
{
    private final Executor executor;
    private final Connection connection;

    public FabricMessageContext(Executor executor, Connection connection, @Nullable ServerPlayer player, MessageDirection direction)
    {
        super(direction, player);
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
