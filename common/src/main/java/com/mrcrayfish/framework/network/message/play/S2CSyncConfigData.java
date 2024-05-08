package com.mrcrayfish.framework.network.message.play;

import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.client.multiplayer.ClientPlayHandler;
import com.mrcrayfish.framework.network.FrameworkCodecs;
import com.mrcrayfish.framework.util.Utils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public record S2CSyncConfigData(ResourceLocation id, byte[] data)
{
    public static final StreamCodec<RegistryFriendlyByteBuf, S2CSyncConfigData> STREAM_CODEC = StreamCodec.composite(
        ResourceLocation.STREAM_CODEC,
        S2CSyncConfigData::id,
        FrameworkCodecs.BYTE_ARRAY,
        S2CSyncConfigData::data,
        S2CSyncConfigData::new
    );

    public static void handle(S2CSyncConfigData message, MessageContext context)
    {
        context.execute(() -> ClientPlayHandler.handleSyncConfigData(context, message));
        context.setHandled(true);
    }
}
