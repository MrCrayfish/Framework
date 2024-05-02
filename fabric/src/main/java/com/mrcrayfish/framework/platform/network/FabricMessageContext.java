package com.mrcrayfish.framework.platform.network;

import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.world.entity.player.Player;

import org.jetbrains.annotations.Nullable;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

/**
 * Author: MrCrayfish
 */
public class FabricMessageContext extends MessageContext
{
    private final Executor executor;
    private final Consumer<Component> disconnect;

    public FabricMessageContext(Executor executor, Consumer<Component> disconnect, @Nullable Player player, PacketFlow flow)
    {
        super(flow, player);
        this.executor = executor;
        this.disconnect = disconnect;
    }

    @Override
    public void execute(Runnable runnable)
    {
        this.executor.execute(runnable);
    }

    @Override
    public void disconnect(Component reason)
    {
        this.disconnect.accept(reason);
    }

    @Override
    public void setHandled(boolean handled) {}
}
