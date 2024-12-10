package com.mrcrayfish.framework.platform.network;

import com.mrcrayfish.framework.api.network.ConfigurationMessageContext;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.PlayMessageContext;
import com.mrcrayfish.framework.network.message.ConfigurationMessage;
import com.mrcrayfish.framework.network.message.FrameworkMessage;
import com.mrcrayfish.framework.network.message.FrameworkPayload;
import com.mrcrayfish.framework.network.message.PlayMessage;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientConfigurationPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;

/**
 * Author: MrCrayfish
 */
public class FabricClientNetworkHandler
{
    public static <T> void receivePlay(PlayMessage<T> message, FrameworkPayload<T> payload, FabricNetwork network, ClientPlayNetworking.Context context)
    {
        Minecraft minecraft = context.client();
        PlayMessageContext ctx = new PlayMessageContext(message.flow(), minecraft, context.responseSender()::disconnect, b -> {}, minecraft.player);
        message.handler().accept(payload.msg(), ctx);
        ctx.getReply().ifPresent(msg -> context.responseSender().sendPacket(ClientPlayNetworking.createC2SPacket(network.encode(msg))));
    }

    public static <T> void receiveConfiguration(ConfigurationMessage<T> message, FrameworkPayload<T> payload, FabricNetwork network, ClientConfigurationNetworking.Context context)
    {
        Minecraft minecraft = Minecraft.getInstance();
        ConfigurationMessageContext ctx = new ConfigurationMessageContext(message.flow(), minecraft, context.responseSender()::disconnect, b -> {}, id -> {});
        message.handler().accept(payload.msg(), ctx);
        ctx.getReply().ifPresent(msg -> context.responseSender().sendPacket(ClientPlayNetworking.createC2SPacket(network.encode(msg))));
    }
}
