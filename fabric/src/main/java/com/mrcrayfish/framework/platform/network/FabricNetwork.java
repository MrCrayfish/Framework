package com.mrcrayfish.framework.platform.network;

import com.mrcrayfish.framework.api.Environment;
import com.mrcrayfish.framework.api.FrameworkAPI;
import com.mrcrayfish.framework.api.network.FrameworkNetwork;
import com.mrcrayfish.framework.api.network.FrameworkResponse;
import com.mrcrayfish.framework.api.network.LevelLocation;
import com.mrcrayfish.framework.api.util.TaskRunner;
import com.mrcrayfish.framework.network.message.FrameworkMessage;
import com.mrcrayfish.framework.network.message.FrameworkPayload;
import com.mrcrayfish.framework.network.message.PlayMessage;
import com.mrcrayfish.framework.platform.Services;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.SectionPos;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
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
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public final class FabricNetwork implements FrameworkNetwork
{
    final ResourceLocation id;
    final int protocolVersion;
    final List<PlayMessage<?>> playMessages;
    final List<FrameworkMessage<?, FriendlyByteBuf>> configurationMessages;
    final Map<Class<?>, FrameworkMessage<?, ? extends FriendlyByteBuf>> classToMessage;
    final List<BiFunction<FabricNetwork, ServerConfigurationPacketListenerImpl, ConfigurationTask>> configurationTasks;
    final FrameworkMessage pingMessage;
    private MinecraftServer server;
    private boolean active = false;

    public FabricNetwork(ResourceLocation id, int protocolVersion, List<PlayMessage<?>> playMessages, List<FrameworkMessage<?, FriendlyByteBuf>> configurationMessages, List<BiFunction<FabricNetwork, ServerConfigurationPacketListenerImpl, ConfigurationTask>> configurationTasks)
    {
        this.id = id;
        this.protocolVersion = protocolVersion;
        this.playMessages = playMessages;
        this.configurationMessages = configurationMessages;
        this.configurationTasks = configurationTasks;
        this.classToMessage = createClassMap(playMessages, configurationMessages);
        this.pingMessage = this.classToMessage.get(Ping.class);
        this.setup();
    }

    private void setup()
    {
        this.playMessages.forEach(message -> {
            if(message.flow() == PacketFlow.CLIENTBOUND || message.flow() == null) {
                this.registerPlayS2C(message);
            }
            if(message.flow() == PacketFlow.SERVERBOUND || message.flow() == null) {
                this.registerPlayC2S(message);
            }
        });

        // Ping lets the client know that this network is also running on the server.
        // If no ping is received, it's most likely the mod is not installed on the server.
        PayloadTypeRegistry.configurationS2C().register(this.pingMessage.type(), this.pingMessage.codec());
        PayloadTypeRegistry.configurationC2S().register(this.pingMessage.type(), this.pingMessage.codec());
        TaskRunner.runIf(Environment.CLIENT, () -> () -> {
            ClientConfigurationConnectionEvents.INIT.register((handler, client) -> {
                ClientConfigurationNetworking.registerReceiver(this.pingMessage.type(), (payload, context) -> {
                    this.active = true;
                });
            });
            ClientPlayConnectionEvents.DISCONNECT.register((handler1, client1) -> {
                this.active = false;
            });
        });

        // Register client bound and bidirectional messages on physical client
        this.configurationMessages.stream()
            .filter(message -> message.flow() == null || message.flow() == PacketFlow.CLIENTBOUND)
            .forEach(this::registerConfigurationS2C);

        // Register server bound and bidirectional messages on client/server
        this.configurationMessages.stream()
            .filter(message -> message.flow() == null || message.flow() == PacketFlow.SERVERBOUND)
            .forEach(this::registerConfigurationC2S);

        // Add configuration tasks
        ServerConfigurationConnectionEvents.CONFIGURE.register((handler, server) -> {
            this.configurationTasks.forEach(function -> {
                FabricConfigurationTask<?> task = (FabricConfigurationTask<?>) function.apply(this, handler);
                if(ServerConfigurationNetworking.canSend(handler, task.id())) {
                    handler.addTask(task);
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
    }

    private <T> void registerPlayS2C(PlayMessage<T> message)
    {
        PayloadTypeRegistry.playS2C().register(message.type(), message.codec());
        Optional.ofNullable(message.flow()).ifPresent(flow ->
            PayloadTypeRegistry.playC2S().register(message.type(), message.codec())
        );
        TaskRunner.runIf(Environment.CLIENT, () -> () -> {
            ClientPlayNetworking.registerGlobalReceiver(message.type(), (payload, context) -> {
                FabricClientNetworkHandler.receivePlay(message, payload, this, context);
            });
        });
    }

    private <T> void registerPlayC2S(PlayMessage<T> message)
    {
        PayloadTypeRegistry.playC2S().register(message.type(), message.codec());
        Optional.ofNullable(message.flow()).ifPresent(flow ->
            PayloadTypeRegistry.playS2C().register(message.type(), message.codec())
        );
        ServerPlayNetworking.registerGlobalReceiver(message.type(), (payload, context) -> {
            FabricServerNetworkHandler.receivePlay(message, payload, this, context);
        });
    }

    private <T> void registerConfigurationS2C(FrameworkMessage<T, FriendlyByteBuf> message)
    {
        if(message == this.pingMessage)
            return;
        PayloadTypeRegistry.configurationS2C().register(message.type(), message.codec());
        Optional.ofNullable(message.flow()).ifPresent(flow ->
            PayloadTypeRegistry.configurationC2S().register(message.type(), message.codec())
        );
        TaskRunner.runIf(Environment.CLIENT, () -> () -> {
            ClientConfigurationConnectionEvents.INIT.register((handler, client) -> {
                ClientConfigurationNetworking.registerGlobalReceiver(message.type(), (payload, context) -> {
                    FabricClientNetworkHandler.receiveConfiguration(message, payload, this, context);
                });
            });
        });
    }

    private <T> void registerConfigurationC2S(FrameworkMessage<T, FriendlyByteBuf> message)
    {
        if(message == this.pingMessage)
            return;
        PayloadTypeRegistry.configurationC2S().register(message.type(), message.codec());
        Optional.ofNullable(message.flow()).ifPresent(flow ->
            PayloadTypeRegistry.configurationS2C().register(message.type(), message.codec())
        );
        ServerConfigurationNetworking.registerGlobalReceiver(message.type(), (payload, context) -> {
            FabricServerNetworkHandler.receiveConfiguration(message, payload, this, context);
        });
    }

    @Override
    public void send(Connection connection, Object message)
    {
        switch(connection.getSending())
        {
            case SERVERBOUND -> connection.send(ClientPlayNetworking.createC2SPacket(this.encode(message)));
            case CLIENTBOUND -> connection.send(ServerPlayNetworking.createS2CPacket(this.encode(message)));
        }
    }

    @Override
    public void sendToPlayer(Supplier<ServerPlayer> supplier, Object message)
    {
        ServerPlayNetworking.send(supplier.get(), this.encode(message));
    }

    @Override
    public void sendToTrackingEntity(Supplier<Entity> supplier, Object message)
    {
        Entity entity = supplier.get();
        Packet<ClientCommonPacketListener> packet = ServerPlayNetworking.createS2CPacket(this.encode(message));
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
        Packet<ClientCommonPacketListener> packet = ServerPlayNetworking.createS2CPacket(this.encode(message));
        ((ServerChunkCache) chunk.getLevel().getChunkSource()).chunkMap.getPlayers(chunk.getPos(), false).forEach(e -> e.connection.send(packet));
    }

    @Override
    public void sendToNearbyPlayers(Supplier<LevelLocation> supplier, Object message)
    {
        LevelLocation location = supplier.get();
        Level level = location.level();
        Vec3 pos = location.pos();
        Packet<ClientCommonPacketListener> packet = ServerPlayNetworking.createS2CPacket(this.encode(message));
        this.server.getPlayerList().broadcast(null, pos.x, pos.y, pos.z, location.range(), level.dimension(), packet);
    }

    @Override
    public void sendToServer(Object message)
    {
        ClientPlayNetworking.send(this.encode(message));
    }

    @Override
    public void sendToAll(Object message)
    {
        Packet<ClientCommonPacketListener> packet = ServerPlayNetworking.createS2CPacket(this.encode(message));
        this.server.getPlayerList().broadcastAll(packet);
    }

    @Override
    public boolean isActive(Connection connection)
    {
        return !FrameworkAPI.getEnvironment().isClient() || this.active;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    <T> FrameworkPayload<T> encode(Object message)
    {
        FrameworkMessage msg = this.classToMessage.get(message.getClass());
        if(msg == null) throw new IllegalArgumentException("Unregistered message: " + message.getClass().getName());
        return msg.writePayload(message);
    }

    private static Map<Class<?>, FrameworkMessage<?, ? extends FriendlyByteBuf>> createClassMap(Collection<PlayMessage<?>> a, List<FrameworkMessage<?, FriendlyByteBuf>> b)
    {
        Object2ObjectMap<Class<?>, FrameworkMessage<?, ?>> map = new Object2ObjectArrayMap<>();
        a.forEach(msg -> map.put(msg.messageClass(), msg));
        b.forEach(msg -> map.put(msg.messageClass(), msg));
        return Collections.unmodifiableMap(map);
    }

    /**
     * Simple message to let client know if a network is active on the server
     */
    record Ping()
    {
        private static final Ping INSTANCE = new Ping();
        public static final StreamCodec<FriendlyByteBuf, Ping> STREAM_CODEC = StreamCodec.unit(INSTANCE);

        public static void encode(Ping message, FriendlyByteBuf buffer) {}

        public static Ping decode(FriendlyByteBuf buffer)
        {
            return new Ping();
        }

        public static FrameworkResponse handle(Ping message, Consumer<Runnable> executor)
        {
            return FrameworkResponse.success();
        }
    }
}
