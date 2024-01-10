package com.mrcrayfish.framework.network.message.play;

import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.client.multiplayer.ClientPlayHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public record S2CSyncConfigData(ResourceLocation id, byte[] data)
{
    public static void encode(S2CSyncConfigData message, FriendlyByteBuf buffer)
    {
        buffer.writeResourceLocation(message.id);
        buffer.writeByteArray(message.data);
    }

    public static S2CSyncConfigData decode(FriendlyByteBuf buffer)
    {
        return new S2CSyncConfigData(buffer.readResourceLocation(), buffer.readByteArray());
    }

    public static void handle(S2CSyncConfigData message, MessageContext context)
    {
        context.execute(() -> ClientPlayHandler.handleSyncConfigData(context, message));
        context.setHandled(true);
    }
}
