package com.mrcrayfish.framework.platform.network;

import com.mrcrayfish.framework.api.network.FrameworkNetwork;
import com.mrcrayfish.framework.api.network.LevelLocation;
import com.mrcrayfish.framework.network.message.IMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class ForgeNetwork implements FrameworkNetwork
{
    private final SimpleChannel channel;

    public ForgeNetwork(SimpleChannel channel)
    {
        this.channel = channel;
    }

    @Override
    public void sendToPlayer(Supplier<ServerPlayer> supplier, IMessage<?> message)
    {
        this.channel.send(PacketDistributor.PLAYER.with(supplier), message);
    }

    @Override
    @Deprecated
    public void sendToTracking(Supplier<Entity> supplier, IMessage<?> message)
    {
        this.sendToTrackingEntity(supplier, message);
    }

    @Override
    public void sendToTrackingEntity(Supplier<Entity> supplier, IMessage<?> message)
    {
        this.channel.send(PacketDistributor.TRACKING_ENTITY.with(supplier), message);
    }

    @Override
    public void sendToTrackingBlockEntity(Supplier<BlockEntity> supplier, IMessage<?> message)
    {
        this.sendToTrackingChunk(() -> {
            BlockEntity entity = supplier.get();
            return entity.getLevel().getChunkAt(entity.getBlockPos());
        }, message);
    }

    @Override
    public void sendToTrackingLocation(Supplier<LevelLocation> supplier, IMessage<?> message)
    {
        this.sendToTrackingChunk(() -> {
            LevelLocation location = supplier.get();
            Vec3 pos = location.pos();
            int chunkX = SectionPos.blockToSectionCoord(pos.x);
            int chunkZ = SectionPos.blockToSectionCoord(pos.z);
            return location.level().getChunk(chunkX, chunkZ);
        }, message);
    }

    @Override
    public void sendToTrackingChunk(Supplier<LevelChunk> supplier, IMessage<?> message)
    {
        this.channel.send(PacketDistributor.TRACKING_CHUNK.with(supplier), message);
    }

    @Override
    public void sendToNearbyPlayers(Supplier<LevelLocation> supplier, IMessage<?> message)
    {
        this.channel.send(PacketDistributor.NEAR.with(() -> {
            LevelLocation location = supplier.get();
            Vec3 pos = location.pos();
            return new PacketDistributor.TargetPoint(pos.x, pos.y, pos.z, location.range(), location.level().dimension());
        }), message);
    }

    @Override
    public void sendToServer(IMessage<?> message)
    {
        this.channel.sendToServer(message);
    }

    @Override
    public void sendToAll(IMessage<?> message)
    {
        this.channel.send(PacketDistributor.ALL.noArg(), message);
    }

    @Override
    public boolean isActive(Connection connection)
    {
        return this.channel.isRemotePresent(connection);
    }
}
