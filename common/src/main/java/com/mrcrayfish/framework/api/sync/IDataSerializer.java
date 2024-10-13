package com.mrcrayfish.framework.api.sync;

import com.mrcrayfish.framework.entity.sync.Updatable;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Author: MrCrayfish
 */
public interface IDataSerializer<T>
{
    void write(FriendlyByteBuf buf, T value);

    T read(FriendlyByteBuf buf);

    @Deprecated(forRemoval = true, since = "0.7.9")
    default T read(Updatable updatable, FriendlyByteBuf buf) {
        return read(buf);
    }

    Tag write(T value);

    T read(Tag nbt);

    @Deprecated(forRemoval = true, since = "0.7.9")
    default T read(Updatable updatable, Tag nbt) {
        return read(nbt);
    }
}
