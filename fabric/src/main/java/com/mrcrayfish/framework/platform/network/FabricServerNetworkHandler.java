package com.mrcrayfish.framework.platform.network;

import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.network.message.FrameworkMessage;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

import org.jetbrains.annotations.Nullable;

/**
 * Author: MrCrayfish
 */
public class FabricServerNetworkHandler
{
    static <T> void receivePlay(FrameworkMessage<T> message, FabricNetwork network, MinecraftServer server, @Nullable ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender sender)
    {
        T msg = message.decoder().apply(buf);
        MessageContext context = new FabricMessageContext(server, handler.connection, player, PacketFlow.SERVERBOUND);
        message.handler().accept(msg, context);
        context.getReply().ifPresent(reply -> sender.sendPacket(message.id(), network.encode(reply)));
    }

    static <T> void receiveConfiguration(FrameworkMessage<T> message, FabricNetwork network, MinecraftServer server, ServerConfigurationPacketListenerImpl handler, FriendlyByteBuf buf, PacketSender sender)
    {
        T msg = message.decoder().apply(buf);
        MessageContext context = new FabricMessageContext(server, handler.connection, null, PacketFlow.SERVERBOUND);
        message.handler().accept(msg, context);
        context.getReply().ifPresent(reply -> sender.sendPacket(message.id(), network.encode(reply)));
    }
}
