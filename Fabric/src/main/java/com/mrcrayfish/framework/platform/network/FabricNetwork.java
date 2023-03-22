package com.mrcrayfish.framework.platform.network;

import com.google.common.base.Preconditions;
import com.mrcrayfish.framework.api.Environment;
import com.mrcrayfish.framework.api.network.FrameworkNetwork;
import com.mrcrayfish.framework.api.network.MessageDirection;
import com.mrcrayfish.framework.api.util.EnvironmentHelper;
import com.mrcrayfish.framework.network.message.IMessage;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.fabricmc.fabric.api.client.networking.v1.C2SPlayChannelEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
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
    final Map<Class<?>, FabricHandshakeMessage<?>> classToHandshakeMessage;
    final Map<Integer, FabricHandshakeMessage<?>> indexToHandshakeMessage;
    private MinecraftServer server;

    public FabricNetwork(ResourceLocation id, int protocolVersion, List<FabricMessage<?>> playMessages, List<FabricHandshakeMessage<?>> handshakeMessages)
    {
        this.id = id;
        this.protocolVersion = protocolVersion;
        this.classToPlayMessage = createClassMap(playMessages);
        this.indexToPlayMessage = createIndexMap(playMessages);
        this.classToHandshakeMessage = createClassMap(handshakeMessages);
        this.indexToHandshakeMessage = createIndexMap(handshakeMessages);
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
        }

        // Register receivers for login messages and register events
        if(!this.classToHandshakeMessage.isEmpty())
        {
            // Only register client receiver only if on physical client
            EnvironmentHelper.runOn(Environment.CLIENT, () -> () -> {
                ClientLoginNetworking.registerGlobalReceiver(this.id, (client, handler, buf, responseSender) -> {
                    return CompletableFuture.completedFuture(FabricClientNetworkHandler.receiveHandshake(this, client, handler, buf, responseSender));
                });
            });
            // Sends the login messages to client when they are connecting
            ServerLoginConnectionEvents.QUERY_START.register((handler, server, sender, synchronizer) -> {
                this.sendHandshakeMessages(sender, handler.getConnection().isMemoryConnection());
            });
            ServerLoginNetworking.registerGlobalReceiver(this.id, (server, handler, understood, buf, synchronizer, responseSender) -> {
                FabricServerNetworkHandler.receiveHandshake(this, server, handler, understood, buf, synchronizer, responseSender);
            });
        }

        // Get access to MinecraftServer instances
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            this.server = server;
        });
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            this.server = null;
        });
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
        FriendlyByteBuf buf = this.encode(message);
        Packet<ClientGamePacketListener> packet = ServerPlayNetworking.createS2CPacket(this.id, buf);
        Entity entity = supplier.get();
        ((ServerChunkCache) entity.getCommandSenderWorld().getChunkSource()).broadcast(entity, packet);
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
        Packet<ClientGamePacketListener> packet = ServerPlayNetworking.createS2CPacket(this.id, buf);
        this.server.getPlayerList().broadcastAll(packet);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private FriendlyByteBuf encode(IMessage<?> message)
    {
        FabricMessage fabricMessage = this.classToPlayMessage.get(message.getClass());
        Preconditions.checkNotNull(fabricMessage);
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeInt(fabricMessage.getIndex());
        fabricMessage.encode(message, buf);
        return buf;
    }

    private void sendHandshakeMessages(PacketSender sender, boolean isLocal)
    {
        this.classToHandshakeMessage.values().forEach(fabricMessage ->
        {
            Optional.ofNullable(fabricMessage.getMessages()).ifPresent(messages ->
            {
                messages.apply(isLocal).forEach(pair ->
                {
                    FriendlyByteBuf buf = PacketByteBufs.create();
                    buf.writeInt(fabricMessage.getIndex());
                    this.encodeLoginMessage(pair.getValue(), buf);
                    sender.sendPacket(this.id, buf);
                });
            });
        });
    }

    @SuppressWarnings("unchecked")
    private <T> void encodeLoginMessage(T message, FriendlyByteBuf buf)
    {
        FabricHandshakeMessage<T> msg = (FabricHandshakeMessage<T>) this.classToHandshakeMessage.get(message.getClass());
        msg.encode(message, buf);
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
