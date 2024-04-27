package com.mrcrayfish.framework.platform.network;

import com.google.common.base.Preconditions;
import com.mrcrayfish.framework.api.network.FrameworkNetwork;
import com.mrcrayfish.framework.api.network.LevelLocation;
import com.mrcrayfish.framework.network.message.FrameworkMessage;
import com.mrcrayfish.framework.network.message.FrameworkPayload;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.core.SectionPos;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

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
    private final List<BiConsumer<NeoForgeNetwork, PayloadRegistrar>> playPayloads;
    private final List<BiConsumer<NeoForgeNetwork, PayloadRegistrar>> configurationPayloads;
    private final Map<Class<?>, FrameworkMessage<?, ? extends FriendlyByteBuf>> classToMessage;
    private final List<BiFunction<NeoForgeNetwork, ServerConfigurationPacketListener, ICustomConfigurationTask>> tasks;

    public NeoForgeNetwork(ResourceLocation id, int version, boolean optional, Collection<FrameworkMessage<?, RegistryFriendlyByteBuf>> playMessages, List<BiConsumer<NeoForgeNetwork, PayloadRegistrar>> playPayloads, List<FrameworkMessage<?, FriendlyByteBuf>> configurationMessages, List<BiConsumer<NeoForgeNetwork, PayloadRegistrar>> configurationPayloads, List<BiFunction<NeoForgeNetwork, ServerConfigurationPacketListener, ICustomConfigurationTask>> tasks)
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
        FrameworkMessage<T, ? extends FriendlyByteBuf> msg = (FrameworkMessage<T, ? extends FriendlyByteBuf>) this.classToMessage.get(message.getClass());
        if(msg == null) throw new IllegalArgumentException("Unregistered message: " + message.getClass().getName());
        return msg.writePayload(message);
    }

    public void registerPayloads(PayloadRegistrar registrar)
    {
        registrar = registrar.versioned(Integer.toString(this.version)); // Set the version
        registrar = this.optional ? registrar.optional() : registrar;
        PayloadRegistrar finalRegistrar = registrar;
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
        PacketDistributor.sendToPlayer(supplier.get(), this.encode(message));
    }

    @Override
    public void sendToTrackingEntity(Supplier<Entity> supplier, Object message)
    {
        PacketDistributor.sendToPlayersTrackingEntity(supplier.get(), this.encode(message));
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
        LevelChunk chunk = supplier.get();
        PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) chunk.getLevel(), chunk.getPos(), this.encode(message));
    }

    @Override
    public void sendToNearbyPlayers(Supplier<LevelLocation> supplier, Object message)
    {
        LevelLocation location = supplier.get();
        Vec3 pos = location.pos();
        PacketDistributor.sendToPlayersNear(location.level(), null, pos.x, pos.y, pos.z, location.range(), this.encode(message));
    }

    @Override
    public void sendToServer(Object message)
    {
        PacketDistributor.sendToServer(this.encode(message));
    }

    @Override
    public void sendToAll(Object message)
    {
        PacketDistributor.sendToAllPlayers(this.encode(message));
    }

    @Override
    public boolean isActive(Connection connection)
    {
        if(connection.getPacketListener() instanceof ServerCommonPacketListener listener)
        {
            FrameworkMessage<?, ? extends FriendlyByteBuf> msg = this.classToMessage.values().stream().findAny().orElse(null);
            return msg != null && listener.hasChannel(msg.type());
        }
        else if(connection.getPacketListener() instanceof ClientCommonPacketListener listener)
        {
            FrameworkMessage<?, ? extends FriendlyByteBuf> msg = this.classToMessage.values().stream().findAny().orElse(null);
            return msg != null && listener.hasChannel(msg.type());
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

    private static Map<Class<?>, FrameworkMessage<?, ? extends FriendlyByteBuf>> createClassMap(Collection<FrameworkMessage<?, RegistryFriendlyByteBuf>> a, List<FrameworkMessage<?, FriendlyByteBuf>> b)
    {
        Object2ObjectMap<Class<?>, FrameworkMessage<?, ?>> map = new Object2ObjectArrayMap<>();
        a.forEach(msg -> map.put(msg.messageClass(), msg));
        b.forEach(msg -> map.put(msg.messageClass(), msg));
        return Collections.unmodifiableMap(map);
    }
}
