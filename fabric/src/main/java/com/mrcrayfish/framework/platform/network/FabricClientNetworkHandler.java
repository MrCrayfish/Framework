package com.mrcrayfish.framework.platform.network;

import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.network.message.FrameworkMessage;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientConfigurationPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;

/**
 * Author: MrCrayfish
 */
public class FabricClientNetworkHandler
{
    public static <T> void receivePlay(FrameworkMessage<T> message, FabricNetwork network, Minecraft minecraft, ClientPacketListener listener, FriendlyByteBuf buf, PacketSender sender)
    {
        T msg = message.decoder().apply(buf);
        MessageContext context = new FabricMessageContext(minecraft, listener.getConnection(), minecraft.player, PacketFlow.CLIENTBOUND);
        message.handler().accept(msg, context);
        context.getReply().ifPresent(reply -> sender.sendPacket(message.id(), network.encode(reply)));
    }

    public static <T> void receiveConfiguration(FrameworkMessage<T> message, FabricNetwork network, Minecraft minecraft, ClientConfigurationPacketListenerImpl listener, FriendlyByteBuf buf, PacketSender sender)
    {
        T msg = message.decoder().apply(buf);
        MessageContext context = new FabricMessageContext(minecraft, listener.connection, null, PacketFlow.CLIENTBOUND);
        message.handler().accept(msg, context);
        context.getReply().ifPresent(reply -> sender.sendPacket(message.id(), network.encode(reply)));
    }
}
