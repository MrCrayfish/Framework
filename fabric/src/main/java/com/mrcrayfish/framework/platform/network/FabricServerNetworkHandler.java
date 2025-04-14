package com.mrcrayfish.framework.platform.network;

import com.mrcrayfish.framework.api.network.ConfigurationMessageContext;
import com.mrcrayfish.framework.api.network.PlayMessageContext;
import com.mrcrayfish.framework.network.message.ConfigurationMessage;
import com.mrcrayfish.framework.network.message.FrameworkPayload;
import com.mrcrayfish.framework.network.message.PlayMessage;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ConfigurationTask;

/**
 * Author: MrCrayfish
 */
public class FabricServerNetworkHandler
{
    static <T> void receivePlay(PlayMessage<T> message, FrameworkPayload<T> payload, FabricNetwork network, ServerPlayNetworking.Context context)
    {
        ServerPlayer player = context.player();
        PlayMessageContext ctx = new PlayMessageContext(message.flow(), player.server, context.responseSender()::disconnect, b -> {}, player);
        message.handler().accept(payload.msg(), ctx);
        ctx.getReply().ifPresent(msg -> context.responseSender().sendPacket(network.encode(msg)));
    }

    public static <T> void receiveConfiguration(ConfigurationMessage<T> message, FrameworkPayload<T> payload, FabricNetwork network, ServerConfigurationNetworking.Context context)
    {
        ConfigurationMessageContext ctx = new ConfigurationMessageContext(message.flow(), context.server(), context.responseSender()::disconnect, b -> {}, s -> {
            context.networkHandler().completeTask(new ConfigurationTask.Type(s));
        });
        message.handler().accept(payload.msg(), ctx);
        ctx.getReply().ifPresent(msg -> context.responseSender().sendPacket(network.encode(msg)));
    }
}
