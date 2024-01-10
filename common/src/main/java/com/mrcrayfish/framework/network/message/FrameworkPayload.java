package com.mrcrayfish.framework.network.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiConsumer;

/**
 * Author: MrCrayfish
 */
public record FrameworkPayload<T>(ResourceLocation id, T msg, BiConsumer<T, FriendlyByteBuf> encoder) implements CustomPacketPayload
{
    @Override
    public void write(FriendlyByteBuf buf)
    {
        this.encoder.accept(this.msg, buf);
    }

    @Override
    public ResourceLocation id()
    {
        return this.id;
    }
}
