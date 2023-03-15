package com.mrcrayfish.framework.platform.network;

import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.network.message.IMessage;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
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
        message.handle(msg, new FabricMessageContext(minecraft, listener.getConnection(), message.getDirection()));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static FriendlyByteBuf receiveLogin(FabricNetwork network, Minecraft minecraft, ClientHandshakePacketListenerImpl handler, FriendlyByteBuf buf, Consumer<GenericFutureListener<? extends Future<? super Void>>> consumer)
    {
        int index = buf.readInt();
        FabricMessage message = network.indexToLoginMessage.get(index);
        if(!FabricNetwork.validateMessage(message, handler.getConnection()))
            return null;

        IMessage<?> msg = (IMessage<?>) message.decode(buf);
        MessageContext context = new FabricMessageContext(minecraft, handler.getConnection(), message.getDirection());
        message.handle(msg, context);

        FriendlyByteBuf responseBuf = PacketByteBufs.create();
        IMessage reply = context.getReply();
        if(reply != null)
        {
            message = network.classToLoginMessage.get(reply.getClass());
            responseBuf.writeInt(message.getIndex());
            context.getReply().encode(reply, responseBuf);
        }
        return responseBuf;
    }
}
