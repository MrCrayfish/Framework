package com.mrcrayfish.framework.api.network;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public abstract class MessageContext
{
    private final PacketFlow flow;
    private final @Nullable Player player;
    private Object reply;

    public MessageContext(PacketFlow flow, @Nullable Player player)
    {
        this.flow = flow;
        this.player = player;
    }

    @Nullable
    public PacketFlow getFlow()
    {
        return this.flow;
    }

    public void reply(Object reply)
    {
        this.reply = reply;
    }

    public Optional<Object> getReply()
    {
        return Optional.ofNullable(this.reply);
    }

    public Optional<Player> getPlayer()
    {
        return Optional.ofNullable(this.player);
    }

    public abstract void setHandled(boolean handled);

    public abstract void execute(Runnable runnable);

    public abstract void disconnect(Component reason);
}
