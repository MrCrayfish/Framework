package com.mrcrayfish.framework.api.network;

import com.mrcrayfish.framework.network.message.IMessage;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public interface FrameworkNetwork
{
    void sendToPlayer(Supplier<ServerPlayer> supplier, IMessage<?> message);

    /**
     * Use {@link #sendToTrackingEntity} instead
     */
    @Deprecated
    void sendToTracking(Supplier<Entity> supplier, IMessage<?> message);

    void sendToTrackingEntity(Supplier<Entity> supplier, IMessage<?> message);

    void sendToTrackingBlockEntity(Supplier<BlockEntity> supplier, IMessage<?> message);

    void sendToTrackingLocation(Supplier<LevelLocation> supplier, IMessage<?> message);

    void sendToTrackingChunk(Supplier<LevelChunk> supplier, IMessage<?> message);

    void sendToNearbyPlayers(Supplier<LevelLocation> supplier, IMessage<?> message);

    void sendToServer(IMessage<?> message);

    void sendToAll(IMessage<?> message);

    boolean isActive(Connection connection);
}
