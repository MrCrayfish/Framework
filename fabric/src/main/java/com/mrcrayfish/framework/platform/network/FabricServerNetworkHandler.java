package com.mrcrayfish.framework.platform.network;

import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.network.message.IMessage;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class FabricServerNetworkHandler
{
    @SuppressWarnings({"unchecked", "rawtypes"})
    static void receivePlay(FabricNetwork network, MinecraftServer server, @Nullable ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender sender)
    {
        int index = buf.readInt();
        FabricMessage message = network.indexToPlayMessage.get(index);
        if(!FabricNetwork.validateMessage(message, handler.connection))
            return;

        IMessage<?> msg = (IMessage<?>) message.decode(buf);
        message.handle(msg, new FabricMessageContext(server, handler.connection, player, message.getDirection()));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void receiveConfiguration(FabricNetwork network, MinecraftServer server, ServerConfigurationPacketListenerImpl handler, FriendlyByteBuf buf, PacketSender sender)
    {
        int index = buf.readInt();
        FabricMessage message = network.indexToPlayMessage.get(index);
        if(!FabricNetwork.validateMessage(message, handler.connection))
            return;

        IMessage<?> msg = (IMessage<?>) message.decode(buf);
        MessageContext context = new FabricMessageContext(server, handler.connection, null, message.getDirection());
        message.handle(msg, context);
    }
}
