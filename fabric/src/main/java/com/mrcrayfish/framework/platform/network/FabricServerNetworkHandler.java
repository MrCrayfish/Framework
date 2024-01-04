package com.mrcrayfish.framework.platform.network;

import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.network.message.IMessage;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;

/**
 * Author: MrCrayfish
 */
public class FabricServerNetworkHandler
{
    @SuppressWarnings({"unchecked", "rawtypes"})
    static void receivePlay(FabricNetwork network, MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender sender)
    {
        int index = buf.readInt();
        FabricMessage message = network.indexToPlayMessage.get(index);
        if(!FabricNetwork.validateMessage(message, handler.connection))
            return;

        IMessage<?> msg = (IMessage<?>) message.decode(buf);
        message.handle(msg, new FabricMessageContext(server, handler.connection, player, message.getDirection()));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    static void receiveHandshake(FabricNetwork network, MinecraftServer server, ServerLoginPacketListenerImpl listener, boolean understood, FriendlyByteBuf buf, ServerLoginNetworking.LoginSynchronizer synchronizer, PacketSender sender)
    {
        // Client didn't understand message, so just return
        if(!understood)
            return;

        int index = buf.readInt();
        FabricMessage message = network.indexToHandshakeMessage.get(index);
        if(!FabricNetwork.validateMessage(message, listener.connection))
            return;

        IMessage<?> msg = (IMessage<?>) message.decode(buf);
        MessageContext context = new FabricMessageContext(server, listener.connection, null, message.getDirection());
        message.handle(msg, context);
    }
}
