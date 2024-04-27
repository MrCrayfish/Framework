package com.mrcrayfish.framework.api.sync;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.BiFunction;

/**
 * Author: MrCrayfish
 */
public final class DataSerializer<T>
{
    private final StreamCodec<? super RegistryFriendlyByteBuf, T> codec;
    private final BiFunction<T, HolderLookup.Provider, Tag> writeTag;
    private final BiFunction<Tag, HolderLookup.Provider, T> readTag;

    public <E extends ByteBuf> DataSerializer(StreamCodec<? super RegistryFriendlyByteBuf, T> codec, BiFunction<T, HolderLookup.Provider, Tag> writeTag, BiFunction<Tag, HolderLookup.Provider, T> readTag)
    {
        this.codec = codec;
        this.writeTag = writeTag;
        this.readTag = readTag;
    }

    public StreamCodec<? super RegistryFriendlyByteBuf, T> getCodec()
    {
        return this.codec;
    }

    public BiFunction<T, HolderLookup.Provider, Tag> getTagWriter()
    {
        return this.writeTag;
    }

    public BiFunction<Tag, HolderLookup.Provider, T> getTagReader()
    {
        return this.readTag;
    }
}
