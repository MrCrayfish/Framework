package com.mrcrayfish.framework.platform.network;

import com.mrcrayfish.framework.api.Environment;
import com.mrcrayfish.framework.api.network.FrameworkNetwork;
import com.mrcrayfish.framework.api.network.LevelLocation;
import com.mrcrayfish.framework.api.util.EnvironmentHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.network.GatherLoginConfigurationTasksEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.ConnectionType;
import net.minecraftforge.network.NetworkContext;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public final class ForgeNetwork implements FrameworkNetwork
{
    private final SimpleChannel channel;
    private @Nullable RegistryAccess access = null;

    public ForgeNetwork(ChannelBuilder builder, List<BiConsumer<Supplier<RegistryAccess>, SimpleChannel>> playMessages, List<Consumer<SimpleChannel>> configurationMessages, List<Function<SimpleChannel, ConfigurationTask>> tasks)
    {
        this.channel = builder.simpleChannel();
        playMessages.forEach(c -> c.accept(this::getRegistryAccess, this.channel));
        configurationMessages.forEach(c -> c.accept(this.channel));
        MinecraftForge.EVENT_BUS.addListener((GatherLoginConfigurationTasksEvent event) -> {
            NetworkContext context = NetworkContext.get(event.getConnection());
            if(context.getType() == ConnectionType.MODDED) {
                tasks.forEach(f -> event.addTask(f.apply(this.channel)));
            }
        });
        MinecraftForge.EVENT_BUS.addListener((ServerStartingEvent event) -> {
            this.access = event.getServer().registryAccess();
        });
        MinecraftForge.EVENT_BUS.addListener((ServerStoppedEvent event) -> {
            this.access = null;
        });
    }

    public RegistryAccess getRegistryAccess()
    {
        if(this.access != null)
            return access;
        RegistryAccess local = EnvironmentHelper.callOn(Environment.CLIENT, () -> () -> {
            Minecraft mc = Minecraft.getInstance();
            return mc.level != null ? mc.level.registryAccess() : null;
        });
        if(local != null)
            return local;
        throw new RuntimeException("Failed to retrieve registry access");
    }

    @Override
    public void send(Connection connection, Object message)
    {
        this.channel.send(message, connection);
    }

    @Override
    public void sendToPlayer(Supplier<ServerPlayer> supplier, Object message)
    {
        this.channel.send(message, PacketDistributor.PLAYER.with(supplier.get()));
    }

    @Override
    public void sendToTrackingEntity(Supplier<Entity> supplier, Object message)
    {
        this.channel.send(message, PacketDistributor.TRACKING_ENTITY.with(supplier.get()));
    }

    @Override
    public void sendToTrackingBlockEntity(Supplier<BlockEntity> supplier, Object message)
    {
        this.sendToTrackingChunk(() -> {
            BlockEntity entity = supplier.get();
            return entity.getLevel().getChunkAt(entity.getBlockPos());
        }, message);
    }

    @Override
    public void sendToTrackingLocation(Supplier<LevelLocation> supplier, Object message)
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
    public void sendToTrackingChunk(Supplier<LevelChunk> supplier, Object message)
    {
        this.channel.send(message, PacketDistributor.TRACKING_CHUNK.with(supplier.get()));
    }

    @Override
    public void sendToNearbyPlayers(Supplier<LevelLocation> supplier, Object message)
    {
        LevelLocation location = supplier.get();
        Vec3 pos = location.pos();
        PacketDistributor.TargetPoint point = new PacketDistributor.TargetPoint(pos.x, pos.y, pos.z, location.range(), location.level().dimension());
        this.channel.send(message, PacketDistributor.NEAR.with(point));
    }

    @Override
    public void sendToServer(Object message)
    {
        this.channel.send(message, PacketDistributor.SERVER.noArg());
    }

    @Override
    public void sendToAll(Object message)
    {
        this.channel.send(message, PacketDistributor.ALL.noArg());
    }

    @Override
    public boolean isActive(Connection connection)
    {
        return this.channel.isRemotePresent(connection);
    }
}
