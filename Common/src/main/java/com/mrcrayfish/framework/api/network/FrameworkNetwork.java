package com.mrcrayfish.framework.api.network;

import com.mrcrayfish.framework.network.message.IMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public interface FrameworkNetwork
{
    void sendToPlayer(Supplier<ServerPlayer> supplier, IMessage<?> message);

    void sendToTracking(Supplier<Entity> supplier, IMessage<?> message);

    void sendToServer(IMessage<?> message);

    void sendToAll(IMessage<?> message);
}
