package com.mrcrayfish.framework.api.sync;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Author: MrCrayfish
 */
public record DataSerializer<T>(StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec, BiConsumer<ValueOutput, T> writer, Function<ValueInput, @Nullable T> reader)
{
    public DataSerializer(StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec, Codec<T> codec)
    {
        this(streamCodec, (output, value) -> output.store("Value", codec, value), input -> input.read("Value", codec).orElse(null));
    }
}
