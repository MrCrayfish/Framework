package com.mrcrayfish.framework.entity.sync;

import com.mrcrayfish.framework.api.sync.SyncedDataKey;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class DataEntry<E extends Entity, T>
{
    private final SyncedDataKey<E, T> key;
    private T value;
    private boolean dirty;

    DataEntry(SyncedDataKey<E, T> key)
    {
        this.key = key;
        this.value = key.defaultValueSupplier().get();
    }

    SyncedDataKey<E, T> getKey()
    {
        return this.key;
    }

    T getValue()
    {
        return this.value;
    }

    void setValue(T value, boolean dirty)
    {
        this.value = value;
        this.dirty = dirty;
    }

    boolean isDirty()
    {
        return this.dirty;
    }

    void clean()
    {
        this.dirty = false;
    }

    public void write(RegistryFriendlyByteBuf buffer)
    {
        int id = SyncedEntityData.instance().getInternalId(this.key);
        buffer.writeVarInt(id);
        this.key.serializer().getCodec().encode(buffer, this.value);
    }

    public static DataEntry<?, ?> read(RegistryFriendlyByteBuf buffer)
    {
        SyncedDataKey<?, ?> key = SyncedEntityData.instance().getKey(buffer.readVarInt());
        Validate.notNull(key, "Synced key does not exist for id");
        DataEntry<?, ?> entry = new DataEntry<>(key);
        entry.readValue(buffer);
        return entry;
    }

    private void readValue(RegistryFriendlyByteBuf buffer)
    {
        this.value = this.getKey().serializer().getCodec().decode(buffer);
    }

    @Nullable
    Tag writeValue(HolderLookup.Provider provider)
    {
        return this.key.serializer().getTagWriter().apply(this.value, provider);
    }

    void readValue(@Nullable Tag tag, HolderLookup.Provider provider)
    {
        this.value = this.key.serializer().getTagReader().apply(tag, provider);
    }
}
