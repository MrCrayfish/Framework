package com.mrcrayfish.framework.platform.network;

import com.mrcrayfish.framework.api.network.FrameworkNetwork;
import com.mrcrayfish.framework.api.network.LevelLocation;
import com.mrcrayfish.framework.network.message.FrameworkMessage;
import com.mrcrayfish.framework.network.message.FrameworkPayload;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.core.SectionPos;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
import net.neoforged.neoforge.network.registration.NetworkRegistry;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public final class NeoForgeNetwork implements FrameworkNetwork
{
    public static final Set<NeoForgeNetwork> ALL_NETWORKS = new HashSet<>();
    
    private final ResourceLocation id;
    private final int version;
    private final boolean optional;
    private final List<BiConsumer<NeoForgeNetwork, IPayloadRegistrar>> playPayloads;
    private final List<BiConsumer<NeoForgeNetwork, IPayloadRegistrar>> configurationPayloads;
    private final Map<Class<?>, FrameworkMessage<?>> classToMessage;
    private final List<BiFunction<NeoForgeNetwork, ServerConfigurationPacketListener, ICustomConfigurationTask>> tasks;

    public NeoForgeNetwork(ResourceLocation id, int version, boolean optional, List<FrameworkMessage<?>> playMessages, List<BiConsumer<NeoForgeNetwork, IPayloadRegistrar>> playPayloads, List<FrameworkMessage<?>> configurationMessages, List<BiConsumer<NeoForgeNetwork, IPayloadRegistrar>> configurationPayloads, List<BiFunction<NeoForgeNetwork, ServerConfigurationPacketListener, ICustomConfigurationTask>> tasks)
    {
        this.id = id;
        this.version = version;
        this.optional = optional;
        this.classToMessage = createClassMap(playMessages, configurationMessages);
        this.playPayloads = playPayloads;
        this.configurationPayloads = configurationPayloads;
        this.tasks = tasks;
        ALL_NETWORKS.add(this);
    }

    public ResourceLocation getId()
    {
        return this.id;
    }

    public List<BiFunction<NeoForgeNetwork, ServerConfigurationPacketListener, ICustomConfigurationTask>> getTasks()
    {
        return this.tasks;
    }

    @SuppressWarnings("unchecked")
    public <T> FrameworkPayload<T> encode(T message)
    {
        FrameworkMessage<T> msg = (FrameworkMessage<T>) this.classToMessage.get(message.getClass());
        if(msg == null) throw new IllegalArgumentException("Unregistered message: " + message.getClass().getName());
        return msg.writePayload(message);
    }

    public void registerPayloads(IPayloadRegistrar registrar)
    {
        registrar = registrar.versioned(Integer.toString(this.version)); // Set the version
        registrar = this.optional ? registrar.optional() : registrar;
        IPayloadRegistrar finalRegistrar = registrar;
        this.playPayloads.forEach(consumer -> consumer.accept(this, finalRegistrar));
        this.configurationPayloads.forEach(consumer -> consumer.accept(this, finalRegistrar));
    }

    @Override
    public void send(Connection connection, Object message)
    {
        if(connection.getSending().isServerbound())
        {
            connection.send(new ServerboundCustomPayloadPacket(this.encode(message)));
        }
        else
        {
            connection.send(new ClientboundCustomPayloadPacket(this.encode(message)));
        }
    }

    @Override
    public void sendToPlayer(Supplier<ServerPlayer> supplier, Object message)
    {
        PacketDistributor.PLAYER.with(supplier.get()).send(this.encode(message));
    }

    @Override
    public void sendToTrackingEntity(Supplier<Entity> supplier, Object message)
    {
        PacketDistributor.TRACKING_ENTITY.with(supplier.get()).send(this.encode(message));
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
        PacketDistributor.TRACKING_CHUNK.with(supplier.get()).send(this.encode(message));
    }

    @Override
    public void sendToNearbyPlayers(Supplier<LevelLocation> supplier, Object message)
    {
        LevelLocation location = supplier.get();
        Vec3 pos = location.pos();
        PacketDistributor.TargetPoint point = new PacketDistributor.TargetPoint(pos.x, pos.y, pos.z, location.range(), location.level().dimension());
        PacketDistributor.NEAR.with(point).send(this.encode(message));
    }

    @Override
    public void sendToServer(Object message)
    {
        PacketDistributor.SERVER.noArg().send(this.encode(message));
    }

    @Override
    public void sendToAll(Object message)
    {
        PacketDistributor.ALL.noArg().send();
    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public boolean isActive(Connection connection)
    {
        if(connection.getPacketListener() instanceof ServerCommonPacketListener listener)
        {
            return NetworkRegistry.getInstance().isConnected(listener, this.id);
        }
        else if(connection.getPacketListener() instanceof ClientCommonPacketListener listener)
        {
            return NetworkRegistry.getInstance().isConnected(listener, this.id);
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return this.id.hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        NeoForgeNetwork that = (NeoForgeNetwork) o;
        return this.id.equals(that.id);
    }

    private static <T extends FrameworkMessage<?>> Map<Class<?>, T> createClassMap(Collection<T> a, Collection<T> b)
    {
        Object2ObjectMap<Class<?>, T> map = new Object2ObjectArrayMap<>();
        a.forEach(msg -> map.put(msg.messageClass(), msg));
        b.forEach(msg -> map.put(msg.messageClass(), msg));
        return Collections.unmodifiableMap(map);
    }
}
