package com.mrcrayfish.framework.platform.network;

import com.mrcrayfish.framework.Registration;
import com.mrcrayfish.framework.api.Environment;
import com.mrcrayfish.framework.api.network.FrameworkNetwork;
import com.mrcrayfish.framework.api.network.LevelLocation;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.util.TaskRunner;
import com.mrcrayfish.framework.network.message.ConfigurationMessage;
import com.mrcrayfish.framework.network.message.FrameworkMessage;
import com.mrcrayfish.framework.network.message.FrameworkPayload;
import com.mrcrayfish.framework.network.message.PlayMessage;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.core.SectionPos;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public final class NeoForgeNetwork implements FrameworkNetwork, Registration.Event
{
    private final ResourceLocation id;
    private final int version;
    private final boolean optional;
    private final List<Function<NeoForgeNetwork, PayloadHolder<?, RegistryFriendlyByteBuf>>> playPayloads;
    private final List<Function<NeoForgeNetwork, PayloadHolder<?, FriendlyByteBuf>>> configurationPayloads;
    private final Map<Class<?>, FrameworkMessage<?, ? extends FriendlyByteBuf, ? extends MessageContext>> classToMessage;
    private final List<BiFunction<NeoForgeNetwork, ServerConfigurationPacketListener, ICustomConfigurationTask>> tasks;
    private boolean registered = false;

    public NeoForgeNetwork(ResourceLocation id, int version, boolean optional, Collection<PlayMessage<?>> playMessages, List<Function<NeoForgeNetwork, PayloadHolder<?, RegistryFriendlyByteBuf>>> playPayloads, List<ConfigurationMessage<?>> configurationMessages, List<Function<NeoForgeNetwork, PayloadHolder<?, FriendlyByteBuf>>> configurationPayloads, List<BiFunction<NeoForgeNetwork, ServerConfigurationPacketListener, ICustomConfigurationTask>> tasks)
    {
        this.id = id;
        this.version = version;
        this.optional = optional;
        this.classToMessage = createClassMap(playMessages, configurationMessages);
        this.playPayloads = playPayloads;
        this.configurationPayloads = configurationPayloads;
        this.tasks = tasks;
    }

    @Override
    public ResourceLocation id()
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
        this.throwIfUnregisteredNetwork();
        FrameworkMessage<T, ? extends FriendlyByteBuf, ? extends MessageContext> msg = (FrameworkMessage<T, ? extends FriendlyByteBuf, ? extends MessageContext>) this.classToMessage.get(message.getClass());
        if(msg == null) throw new IllegalArgumentException("Unregistered message: " + message.getClass().getName());
        return msg.writePayload(message);
    }

    public void registerPayloads(PayloadRegistrar registrar)
    {
        registrar = registrar.versioned(Integer.toString(this.version)); // Set the version
        registrar = this.optional ? registrar.optional() : registrar;
        PayloadRegistrar finalRegistrar = registrar;
        this.playPayloads.forEach(function -> {
            PayloadHolder<?, RegistryFriendlyByteBuf> holder = function.apply(this);
            this.registerPlayHandler(holder, finalRegistrar);
        });
        this.configurationPayloads.forEach(function -> {
            PayloadHolder<?, FriendlyByteBuf> holder = function.apply(this);
            this.registerConfigurationHandler(holder, finalRegistrar);
        });
    }

    private <T> void registerPlayHandler(PayloadHolder<T, RegistryFriendlyByteBuf> holder, PayloadRegistrar registrar)
    {
        switch(holder.flow())
        {
            case null -> registrar.playBidirectional(holder.type(), holder.codec(), holder.handler());
            case SERVERBOUND -> registrar.playToServer(holder.type(), holder.codec(), holder.handler());
            case CLIENTBOUND -> registrar.playToClient(holder.type(), holder.codec());
        }
    }

    private <T> void registerConfigurationHandler(PayloadHolder<T, FriendlyByteBuf> holder, PayloadRegistrar registrar)
    {
        switch(holder.flow())
        {
            case null -> registrar.configurationBidirectional(holder.type(), holder.codec(), holder.handler());
            case SERVERBOUND -> registrar.configurationToServer(holder.type(), holder.codec(), holder.handler());
            case CLIENTBOUND -> registrar.configurationToClient(holder.type(), holder.codec());
        }
    }

    public <T extends CustomPacketPayload> void registerClientPayloads(RegisterClientPayloadHandlersEvent register)
    {
        this.playPayloads.forEach(function -> {
            PayloadHolder<?, RegistryFriendlyByteBuf> holder = function.apply(this);
            this.registerClientPlayHandler(holder, register);
        });
        this.configurationPayloads.forEach(function -> {
            PayloadHolder<?, FriendlyByteBuf> holder = function.apply(this);
            this.registerClientConfigurationHandler(holder, register);
        });
    }

    private <T> void registerClientPlayHandler(PayloadHolder<T, RegistryFriendlyByteBuf> holder, RegisterClientPayloadHandlersEvent register)
    {
        if(holder.flow() == null || holder.flow() == PacketFlow.CLIENTBOUND)
        {
            register.register(holder.type(), holder.handler());
        }
    }

    private <T> void registerClientConfigurationHandler(PayloadHolder<T, FriendlyByteBuf> holder, RegisterClientPayloadHandlersEvent register)
    {
        if(holder.flow() == null || holder.flow() == PacketFlow.CLIENTBOUND)
        {
            register.register(holder.type(), holder.handler());
        }
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
        TaskRunner.runIf(Environment.CLIENT, () -> () -> {
            ClientPacketDistributor.sendToServer(this.encode(message));
        });
    }

    @Override
    public void sendToAll(Object message)
    {
        PacketDistributor.sendToAllPlayers(this.encode(message));
    }

    @Override
    public boolean isActive(Connection connection)
    {
        this.throwIfUnregisteredNetwork();
        if(connection.getPacketListener() instanceof ServerCommonPacketListener listener)
        {
            FrameworkMessage<?, ? extends FriendlyByteBuf, ? extends MessageContext> msg = this.classToMessage.values().stream().findAny().orElse(null);
            return msg != null && listener.hasChannel(msg.type());
        }
        else if(connection.getPacketListener() instanceof ClientCommonPacketListener listener)
        {
            FrameworkMessage<?, ? extends FriendlyByteBuf, ? extends MessageContext> msg = this.classToMessage.values().stream().findAny().orElse(null);
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

    @Override
    public void onRegistered()
    {
        this.registered = true;
    }

    private void throwIfUnregisteredNetwork()
    {
        if(!this.registered)
        {
            throw new RuntimeException("FrameworkNetwork not registered: " + this.id);
        }
    }

    private static Map<Class<?>, FrameworkMessage<?, ? extends FriendlyByteBuf, ? extends MessageContext>> createClassMap(Collection<PlayMessage<?>> a, List<ConfigurationMessage<?>> b)
    {
        Object2ObjectMap<Class<?>, FrameworkMessage<?, ?, ?>> map = new Object2ObjectArrayMap<>();
        a.forEach(msg -> map.put(msg.messageClass(), msg));
        b.forEach(msg -> map.put(msg.messageClass(), msg));
        return Collections.unmodifiableMap(map);
    }
}
