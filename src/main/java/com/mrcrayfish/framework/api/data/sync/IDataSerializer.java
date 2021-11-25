package com.mrcrayfish.framework.api.data.sync;

import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Author: MrCrayfish
 */
public interface IDataSerializer<T>
{
    void write(FriendlyByteBuf buf, T value);

    T read(FriendlyByteBuf buf);

    Tag write(T value);

    T read(Tag nbt);
}
