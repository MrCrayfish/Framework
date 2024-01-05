package com.mrcrayfish.framework.platform.network;

import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.network.message.IMessage;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientConfigurationPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Consumer;

/**
 * Author: MrCrayfish
 */
public class FabricClientNetworkHandler
{
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void receivePlay(FabricNetwork network, Minecraft minecraft, ClientPacketListener listener, FriendlyByteBuf buf, PacketSender packetSender)
    {
        int index = buf.readInt();
        FabricMessage message = network.indexToPlayMessage.get(index);
        if(!FabricNetwork.validateMessage(message, listener.getConnection()))
            return;

        IMessage<?> msg = (IMessage<?>) message.decode(buf);
        message.handle(msg, new FabricMessageContext(minecraft, listener.getConnection(), null, message.getDirection()));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void receiveConfiguration(FabricNetwork network, Minecraft minecraft, ClientConfigurationPacketListenerImpl listener, FriendlyByteBuf buf, PacketSender packetSender)
    {
        int index = buf.readInt();
        FabricMessage message = network.indexToPlayMessage.get(index);
        if(!FabricNetwork.validateMessage(message, listener.connection))
            return;

        IMessage<?> msg = (IMessage<?>) message.decode(buf);
        MessageContext context = new FabricMessageContext(minecraft, listener.connection, null, message.getDirection());
        message.handle(msg, context);

        IMessage reply = context.getReply();
        if(reply != null)
        {
            FriendlyByteBuf responseBuf = network.encode(reply);
            packetSender.sendPacket(network.id, responseBuf);
        }
    }
}
