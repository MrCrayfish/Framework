package com.mrcrayfish.framework.api.sync;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

/**
 * Author: MrCrayfish
 */
public record DataSerializer<T>(StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec, Codec<T> codec)
{
}
