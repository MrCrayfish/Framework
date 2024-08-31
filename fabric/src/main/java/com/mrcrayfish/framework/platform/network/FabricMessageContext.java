package com.mrcrayfish.framework.platform.network;

import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.world.entity.player.Player;

import org.jetbrains.annotations.Nullable;
import java.util.concurrent.Executor;

/**
 * Author: MrCrayfish
 */
public class FabricMessageContext extends MessageContext
{
    private final Executor executor;
    private final Connection connection;

    public FabricMessageContext(Executor executor, Connection connection, @Nullable Player player, PacketFlow flow)
    {
        super(flow, player);
        this.executor = executor;
        this.connection = connection;
    }

    @Override
    public void execute(Runnable runnable)
    {
        this.executor.execute(runnable);
    }

    @Override
    public void disconnect(Component reason)
    {
        this.connection.disconnect(reason);
    }

    @Override
    public void setHandled(boolean handled) {}
}
