package com.mrcrayfish.framework.api.network;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

/**
 * Author: MrCrayfish
 */
public class PlayMessageContext extends MessageContext
{
    private final @Nullable Player player;

    public PlayMessageContext(PacketFlow flow, Executor executor, Consumer<Component> disconnect, Consumer<Boolean> handled, @Nullable Player player)
    {
        super(flow, executor, disconnect, handled);
        this.player = player;
    }

    public Optional<Player> getPlayer()
    {
        return Optional.ofNullable(this.player);
    }
}
