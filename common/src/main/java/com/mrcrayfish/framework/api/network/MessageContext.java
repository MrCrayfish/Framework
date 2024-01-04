package com.mrcrayfish.framework.api.network;

import com.mrcrayfish.framework.network.message.IMessage;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

/**
 * Author: MrCrayfish
 */
public abstract class MessageContext
{
    private final MessageDirection direction;
    private IMessage<?> reply;
    private ServerPlayer player;

    public MessageContext(MessageDirection direction, ServerPlayer player)
    {
        this.direction = direction;
        this.player = player;
    }

    @Nullable
    public MessageDirection getDirection()
    {
        return this.direction;
    }

    public void reply(IMessage<?> reply)
    {
        this.reply = reply;
    }

    @Nullable
    @SuppressWarnings("rawtypes")
    public IMessage getReply()
    {
        return this.reply;
    }

    @Nullable
    public ServerPlayer getPlayer()
    {
        return this.player;
    }

    public abstract void setHandled(boolean handled);

    public abstract CompletableFuture<Void> execute(Runnable runnable);

    public abstract Connection getNetworkManager();
}
