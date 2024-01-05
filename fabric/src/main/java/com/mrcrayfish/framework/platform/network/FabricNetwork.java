package com.mrcrayfish.framework.platform.network;

import com.google.common.base.Preconditions;
import com.mrcrayfish.framework.api.Environment;
import com.mrcrayfish.framework.api.network.FrameworkNetwork;
import com.mrcrayfish.framework.api.network.LevelLocation;
import com.mrcrayfish.framework.api.network.MessageDirection;
import com.mrcrayfish.framework.api.util.EnvironmentHelper;
import com.mrcrayfish.framework.network.message.IMessage;
import io.netty.channel.ChannelHandler;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.fabricmc.fabric.api.client.networking.v1.C2SPlayChannelEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.S2CPlayChannelEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.impl.recipe.ingredient.CustomIngredientSync;
import net.fabricmc.fabric.impl.recipe.ingredient.SupportedIngredientsPacketEncoder;
import net.fabricmc.fabric.mixin.networking.accessor.ServerCommonNetworkHandlerAccessor;
import net.minecraft.core.SectionPos;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
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

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class FabricNetwork implements FrameworkNetwork
{
    final ResourceLocation id;
    final int protocolVersion;
    final Map<Class<?>, FabricMessage<?>> classToPlayMessage;
    final Map<Integer, FabricMessage<?>> indexToPlayMessage;
    final List<BiFunction<FabricNetwork, ServerConfigurationPacketListenerImpl, ConfigurationTask>> configurationTasks;
    private MinecraftServer server;
    private boolean active = false;

    public FabricNetwork(ResourceLocation id, int protocolVersion, List<FabricMessage<?>> playMessages, List<BiFunction<FabricNetwork, ServerConfigurationPacketListenerImpl, ConfigurationTask>> configurationTasks)
    {
        this.id = id;
        this.protocolVersion = protocolVersion;
        this.classToPlayMessage = createClassMap(playMessages);
        this.indexToPlayMessage = createIndexMap(playMessages);
        this.configurationTasks = configurationTasks;
        this.setup();
    }

    private void setup()
    {
        // Register receivers for play messages
        if(!this.classToPlayMessage.isEmpty())
        {
            // Only register client receiver only if on physical client
            EnvironmentHelper.runOn(Environment.CLIENT, () -> () -> {
                ClientPlayNetworking.registerGlobalReceiver(this.id, (client, handler, buf, responseSender) -> {
                    FabricClientNetworkHandler.receivePlay(this, client, handler, buf, responseSender);
                });
            });
            ServerPlayNetworking.registerGlobalReceiver(this.id, (server, player, handler, buf, responseSender) -> {
                FabricServerNetworkHandler.receivePlay(this, server, player, handler, buf, responseSender);
            });

            EnvironmentHelper.runOn(Environment.CLIENT, () -> () -> {
                ClientConfigurationNetworking.registerGlobalReceiver(this.id, (client, handler, buf, responseSender) -> {
                    FabricClientNetworkHandler.receiveConfiguration(this, client, handler, buf, responseSender);
                });
            });
            ServerConfigurationNetworking.registerGlobalReceiver(this.id, (server1, handler, buf, responseSender) -> {
                FabricServerNetworkHandler.receiveConfiguration(this, server1, handler, buf, responseSender);
            });
            ServerConfigurationConnectionEvents.CONFIGURE.register((handler, server) -> {
                if(ServerConfigurationNetworking.canSend(handler, this.id)) {
                    this.configurationTasks.forEach(function -> handler.addTask(function.apply(this, handler)));
                }
            });
        }

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
    public void send(Connection connection, IMessage<?> message)
    {
        FriendlyByteBuf buf = this.encode(message);
        switch(connection.getSending())
        {
            case SERVERBOUND -> connection.send(ClientPlayNetworking.createC2SPacket(this.id, buf));
            case CLIENTBOUND -> connection.send(ServerPlayNetworking.createS2CPacket(this.id, buf));
        }
    }

    @Override
    public void sendToPlayer(Supplier<ServerPlayer> supplier, IMessage<?> message)
    {
        FriendlyByteBuf buf = this.encode(message);
        ServerPlayNetworking.send(supplier.get(), this.id, buf);
    }

    @Override
    public void sendToTracking(Supplier<Entity> supplier, IMessage<?> message)
    {
        this.sendToTrackingEntity(supplier, message);
    }

    @Override
    public void sendToTrackingEntity(Supplier<Entity> supplier, IMessage<?> message)
    {
        Entity entity = supplier.get();
        FriendlyByteBuf buf = this.encode(message);
        Packet<ClientCommonPacketListener> packet = ServerPlayNetworking.createS2CPacket(this.id, buf);
        ((ServerChunkCache) entity.getCommandSenderWorld().getChunkSource()).broadcast(entity, packet);
    }

    @Override
    public void sendToTrackingBlockEntity(Supplier<BlockEntity> supplier, IMessage<?> message)
    {
        this.sendToTrackingChunk(() -> {
            BlockEntity entity = supplier.get();
            return Objects.requireNonNull(entity.getLevel()).getChunkAt(entity.getBlockPos());
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
        LevelChunk chunk = supplier.get();
        FriendlyByteBuf buf = this.encode(message);
        Packet<ClientCommonPacketListener> packet = ServerPlayNetworking.createS2CPacket(this.id, buf);
        ((ServerChunkCache) chunk.getLevel().getChunkSource()).chunkMap.getPlayers(chunk.getPos(), false).forEach(e -> e.connection.send(packet));
    }

    @Override
    public void sendToNearbyPlayers(Supplier<LevelLocation> supplier, IMessage<?> message)
    {
        LevelLocation location = supplier.get();
        Level level = location.level();
        Vec3 pos = location.pos();
        FriendlyByteBuf buf = this.encode(message);
        Packet<ClientCommonPacketListener> packet = ServerPlayNetworking.createS2CPacket(this.id, buf);
        this.server.getPlayerList().broadcast(null, pos.x, pos.y, pos.z, location.range(), level.dimension(), packet);
    }

    @Override
    public void sendToServer(IMessage<?> message)
    {
        FriendlyByteBuf buf = this.encode(message);
        ClientPlayNetworking.send(this.id, buf);
    }

    @Override
    public void sendToAll(IMessage<?> message)
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

    @SuppressWarnings({"unchecked", "rawtypes"})
    public FriendlyByteBuf encode(IMessage<?> message)
    {
        FabricMessage fabricMessage = this.classToPlayMessage.get(message.getClass());
        Preconditions.checkNotNull(fabricMessage);
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeInt(fabricMessage.getIndex());
        fabricMessage.encode(message, buf);
        return buf;
    }

    private static <T extends FabricMessage<?>> Map<Class<?>, T> createClassMap(Collection<T> c)
    {
        Object2ObjectMap<Class<?>, T> map = new Object2ObjectArrayMap<>();
        c.forEach(msg -> map.put(msg.getMessageClass(), msg));
        return Collections.unmodifiableMap(map);
    }

    private static <T extends FabricMessage<?>> Map<Integer, T> createIndexMap(Collection<T> c)
    {
        Int2ObjectMap<T> map = new Int2ObjectArrayMap<>();
        c.forEach(msg -> map.put(msg.getIndex(), msg));
        return Collections.unmodifiableMap(map);
    }

    static boolean validateMessage(@Nullable FabricMessage<?> message, Connection connection)
    {
        if(message == null)
        {
            connection.disconnect(Component.literal("Received invalid packet, closing connection"));
            return false;
        }
        MessageDirection direction = message.getDirection();
        if(direction != null && !direction.isClient())
        {
            connection.disconnect(Component.literal("Received invalid packet, closing connection"));
            return false;
        }
        return true;
    }
}
