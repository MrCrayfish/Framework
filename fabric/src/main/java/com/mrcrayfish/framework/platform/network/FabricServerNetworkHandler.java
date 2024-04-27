package com.mrcrayfish.framework.platform.network;

import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.network.message.FrameworkMessage;
import com.mrcrayfish.framework.network.message.FrameworkPayload;
import com.mrcrayfish.framework.network.message.PlayMessage;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;

/**
 * Author: MrCrayfish
 */
public class FabricServerNetworkHandler
{
    static <T> void receivePlay(PlayMessage<T> message, FrameworkPayload<T> payload, FabricNetwork network, ServerPlayNetworking.Context context)
    {
        ServerPlayer player = context.player();
        MessageContext ctx = new FabricMessageContext(player.server, context.responseSender()::disconnect, player, message.flow());
        message.handler().accept(payload.msg(), ctx);
        ctx.getReply().ifPresent(msg -> context.responseSender().sendPacket(network.encode(msg)));
    }

    public static <T> void receiveConfiguration(FrameworkMessage<T, FriendlyByteBuf> message, FrameworkPayload<T> payload, FabricNetwork network, ServerConfigurationNetworking.Context context)
    {
        // TODO check
        MessageContext ctx = new FabricMessageContext(null, context.responseSender()::disconnect, null, message.flow());
        message.handler().accept(payload.msg(), ctx);
        ctx.getReply().ifPresent(msg -> context.responseSender().sendPacket(network.encode(msg)));
    }
}
