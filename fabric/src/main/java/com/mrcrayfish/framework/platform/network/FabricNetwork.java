package com.mrcrayfish.framework.platform.network;

import com.google.common.base.Preconditions;
import com.mrcrayfish.framework.api.Environment;
import com.mrcrayfish.framework.api.network.FrameworkNetwork;
import com.mrcrayfish.framework.api.network.LevelLocation;
import com.mrcrayfish.framework.api.util.EnvironmentHelper;
import com.mrcrayfish.framework.network.message.FrameworkMessage;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.fabricmc.fabric.api.client.networking.v1.C2SPlayChannelEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.S2CPlayChannelEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.SectionPos;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public final class FabricNetwork implements FrameworkNetwork
{
    final ResourceLocation id;
    final int protocolVersion;
    final List<FrameworkMessage<?>> playMessages;
    final List<FrameworkMessage<?>> configurationMessages;
    final Map<Class<?>, FrameworkMessage<?>> classToMessage;
    final List<BiFunction<FabricNetwork, ServerConfigurationPacketListenerImpl, ConfigurationTask>> configurationTasks;
    private MinecraftServer server;
    private boolean active = false;

    public FabricNetwork(ResourceLocation id, int protocolVersion, List<FrameworkMessage<?>> playMessages, List<FrameworkMessage<?>> configurationMessages, List<BiFunction<FabricNetwork, ServerConfigurationPacketListenerImpl, ConfigurationTask>> configurationTasks)
    {
        this.id = id;
        this.protocolVersion = protocolVersion;
        this.playMessages = playMessages;
        this.configurationMessages = configurationMessages;
        this.configurationTasks = configurationTasks;
        this.classToMessage = createClassMap(playMessages, configurationMessages);
        this.setup();
    }

    private void setup()
    {
        this.playMessages.forEach(message -> {
            EnvironmentHelper.runOn(Environment.CLIENT, () -> () -> {
                ClientPlayNetworking.registerGlobalReceiver(message.id(), (client, handler, buf, sender) -> {
                    FabricClientNetworkHandler.receivePlay(message, this, client, handler, buf, sender);
                });
            });
            ServerPlayNetworking.registerGlobalReceiver(message.id(), (server1, player, handler, buf, responseSender) -> {
                FabricServerNetworkHandler.receivePlay(message, this, server1, player, handler, buf, responseSender);
            });
        });

        this.configurationMessages.forEach(message -> {
            EnvironmentHelper.runOn(Environment.CLIENT, () -> () -> {
                ClientConfigurationNetworking.registerGlobalReceiver(message.id(), (client, handler, buf, responseSender) -> {
                    FabricClientNetworkHandler.receiveConfiguration(message, this, client, handler, buf, responseSender);
                });
            });
            ServerConfigurationNetworking.registerGlobalReceiver(message.id(), (server1, handler, buf, responseSender) -> {
                FabricServerNetworkHandler.receiveConfiguration(message, this, server1, handler, buf, responseSender);
            });
            ServerConfigurationConnectionEvents.CONFIGURE.register((handler, server) -> {
                if(ServerConfigurationNetworking.canSend(handler, message.id())) {
                    this.configurationTasks.forEach(function -> handler.addTask(function.apply(this, handler)));
                }
            });
        });

        // Get access to MinecraftServer instances
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            this.server = server;
        });
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            this.server = null;
        });

        // Track the registered channels to determine if connected
        EnvironmentHelper.runOn(Environment.CLIENT, () -> () -> {
            C2SPlayChannelEvents.REGISTER.register((handler, sender, client, channels) -> {
                this.active = channels.contains(this.id);
            });
            C2SPlayChannelEvents.UNREGISTER.register((handler, sender, client, channels) -> {
                if(channels.contains(this.id)) {
                    this.active = false;
                }
            });
        });
        EnvironmentHelper.runOn(Environment.DEDICATED_SERVER, () -> () -> {
            S2CPlayChannelEvents.REGISTER.register((handler, sender, server, channels) -> {
                this.active = channels.contains(this.id);
            });
            S2CPlayChannelEvents.UNREGISTER.register((handler, sender, server, channels) -> {
                if(channels.contains(this.id)) {
                    this.active = false;
                }
            });
        });
    }

    @Override
    public void send(Connection connection, Object message)
    {
        FriendlyByteBuf buf = this.encode(message);
        switch(connection.getSending())
        {
            case SERVERBOUND -> connection.send(ClientPlayNetworking.createC2SPacket(this.id, buf));
            case CLIENTBOUND -> connection.send(ServerPlayNetworking.createS2CPacket(this.id, buf));
        }
    }

    @Override
    public void sendToPlayer(Supplier<ServerPlayer> supplier, Object message)
    {
        FriendlyByteBuf buf = this.encode(message);
        ServerPlayNetworking.send(supplier.get(), this.id, buf);
    }

    @Override
    public void sendToTrackingEntity(Supplier<Entity> supplier, Object message)
    {
        Entity entity = supplier.get();
        FriendlyByteBuf buf = this.encode(message);
        Packet<ClientCommonPacketListener> packet = ServerPlayNetworking.createS2CPacket(this.id, buf);
        ((ServerChunkCache) entity.getCommandSenderWorld().getChunkSource()).broadcast(entity, packet);
    }

    @Override
    public void sendToTrackingBlockEntity(Supplier<BlockEntity> supplier, Object message)
    {
        this.sendToTrackingChunk(() -> {
            BlockEntity entity = supplier.get();
            return Objects.requireNonNull(entity.getLevel()).getChunkAt(entity.getBlockPos());
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
        FriendlyByteBuf buf = this.encode(message);
        Packet<ClientCommonPacketListener> packet = ServerPlayNetworking.createS2CPacket(this.id, buf);
        ((ServerChunkCache) chunk.getLevel().getChunkSource()).chunkMap.getPlayers(chunk.getPos(), false).forEach(e -> e.connection.send(packet));
    }

    @Override
    public void sendToNearbyPlayers(Supplier<LevelLocation> supplier, Object message)
    {
        LevelLocation location = supplier.get();
        Level level = location.level();
        Vec3 pos = location.pos();
        FriendlyByteBuf buf = this.encode(message);
        Packet<ClientCommonPacketListener> packet = ServerPlayNetworking.createS2CPacket(this.id, buf);
        this.server.getPlayerList().broadcast(null, pos.x, pos.y, pos.z, location.range(), level.dimension(), packet);
    }

    @Override
    public void sendToServer(Object message)
    {
        FriendlyByteBuf buf = this.encode(message);
        ClientPlayNetworking.send(this.id, buf);
    }

    @Override
    public void sendToAll(Object message)
    {
        FriendlyByteBuf buf = this.encode(message);
        Packet<ClientCommonPacketListener> packet = ServerPlayNetworking.createS2CPacket(this.id, buf);
        this.server.getPlayerList().broadcastAll(packet);
    }

    @Override
    public boolean isActive(Connection connection)
    {
        return connection.isConnected() && this.active;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    FriendlyByteBuf encode(Object message)
    {
        FrameworkMessage msg = this.classToMessage.get(message.getClass());
        Preconditions.checkNotNull(msg);
        FriendlyByteBuf buf = PacketByteBufs.create();
        msg.encoder().accept(message, buf);
        return buf;
    }

    private static <T extends FrameworkMessage<?>> Map<Class<?>, T> createClassMap(Collection<T> a, Collection<T> b)
    {
        Object2ObjectMap<Class<?>, T> map = new Object2ObjectArrayMap<>();
        a.forEach(msg -> map.put(msg.messageClass(), msg));
        b.forEach(msg -> map.put(msg.messageClass(), msg));
        return Collections.unmodifiableMap(map);
    }
}
