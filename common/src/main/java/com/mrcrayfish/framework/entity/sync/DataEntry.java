package com.mrcrayfish.framework.entity.sync;

import com.mrcrayfish.framework.api.sync.SyncedDataKey;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import org.apache.commons.lang3.Validate;

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

    public void write(FriendlyByteBuf buffer)
    {
        int id = SyncedEntityData.instance().getInternalId(this.key);
        buffer.writeVarInt(id);
        this.key.serializer().write(buffer, this.value);
    }

    public static DataEntry<?, ?> read(FriendlyByteBuf buffer)
    {
        SyncedDataKey<?, ?> key = SyncedEntityData.instance().getKey(buffer.readVarInt());
        Validate.notNull(key, "Synced key does not exist for id");
        DataEntry<?, ?> entry = new DataEntry<>(key);
        entry.readValue(buffer);
        return entry;
    }

    private void readValue(FriendlyByteBuf buffer)
    {
        this.value = this.getKey().serializer().read(buffer);
    }

    Tag writeValue()
    {
        return this.key.serializer().write(this.value);
    }

    void readValue(Tag nbt)
    {
        this.value = this.key.serializer().read(nbt);
    }
}
