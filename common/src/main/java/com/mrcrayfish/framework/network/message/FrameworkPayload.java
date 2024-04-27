package com.mrcrayfish.framework.network.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Author: MrCrayfish
 */
public record FrameworkPayload<T>(Type<?> type, T msg) implements CustomPacketPayload
{
    public static <T, B> StreamCodec<B, FrameworkPayload<T>> codec(Type<?> type, StreamCodec<B, T> codec)
    {
        return StreamCodec.composite(codec, FrameworkPayload::msg, msg -> new FrameworkPayload<>(type, msg));
    }

    @Override
    public Type<?> type()
    {
        return this.type;
    }
}
