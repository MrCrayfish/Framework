package com.mrcrayfish.framework.platform.network;

import com.google.common.base.Preconditions;
import com.mrcrayfish.framework.SideExecutor;
import com.mrcrayfish.framework.api.network.MessageDirection;
import com.mrcrayfish.framework.api.network.FrameworkNetwork;
import com.mrcrayfish.framework.network.message.IMessage;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
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
    final Map<Class<?>, FabricLoginMessage<?>> classToLoginMessage;
    final Map<Integer, FabricLoginMessage<?>> indexToLoginMessage;

    public FabricNetwork(ResourceLocation id, int protocolVersion, List<FabricMessage<?>> playMessages, List<FabricLoginMessage<?>> handshakeMessages)
    {
        this.id = id;
        this.protocolVersion = protocolVersion;
        this.classToPlayMessage = createClassMap(playMessages);
        this.indexToPlayMessage = createIndexMap(playMessages);
        this.classToLoginMessage = createClassMap(handshakeMessages);
        this.indexToLoginMessage = createIndexMap(handshakeMessages);
        this.setup();
    }

    private void setup()
    {
        // Register receivers for play messages
        if(!this.classToPlayMessage.isEmpty())
        {
            // Only register client receiver only if on physical client
            SideExecutor.runOn(EnvType.CLIENT, () -> () -> {
                ClientPlayNetworking.registerGlobalReceiver(this.id, (client, handler, buf, responseSender) -> {
                    FabricClientNetworkHandler.receivePlay(this, client, handler, buf, responseSender);
                });
            });
            ServerPlayNetworking.registerGlobalReceiver(this.id, (server, player, handler, buf, responseSender) -> {
                FabricServerNetworkHandler.receivePlay(this, server, player, handler, buf, responseSender);
            });
        }

        // Register receivers for login messages and register events
        if(!this.classToLoginMessage.isEmpty())
        {
            // Only register client receiver only if on physical client
            SideExecutor.runOn(EnvType.CLIENT, () -> () -> {
                ClientLoginNetworking.registerGlobalReceiver(this.id, (client, handler, buf, responseSender) -> {
                    return CompletableFuture.completedFuture(FabricClientNetworkHandler.receiveLogin(this, client, handler, buf, responseSender));
                });
            });
            // Sends the login messages to client when they are connecting
            ServerLoginConnectionEvents.QUERY_START.register((handler, server, sender, synchronizer) -> {
                this.sendLoginMessages(sender, handler.getConnection().isMemoryConnection());
            });
            ServerLoginNetworking.registerGlobalReceiver(this.id, (server, handler, understood, buf, synchronizer, responseSender) -> {
                FabricServerNetworkHandler.receiveLogin(this, server, handler, understood, buf, synchronizer, responseSender);
            });
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

    private void sendLoginMessages(PacketSender sender, boolean isLocal)
    {
        this.classToLoginMessage.values().forEach(fabricMessage ->
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
        FabricLoginMessage<T> fabricLoginMessage = (FabricLoginMessage<T>) this.classToLoginMessage.get(message.getClass());
        fabricLoginMessage.encode(message, buf);
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
