package com.mrcrayfish.framework.entity.sync;

import com.mrcrayfish.framework.api.sync.SyncSignal;
import com.mrcrayfish.framework.api.sync.SyncedDataKey;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import org.apache.commons.lang3.Validate;

import org.jetbrains.annotations.Nullable;

/**
 * Author: MrCrayfish
 */
public class DataEntry<E extends Entity, T>
{
    private final SyncSignal signal;
    private final DataHolder holder;
    private final SyncedDataKey<E, T> key;
    private T value;
    private boolean pendingSync;

    DataEntry(DataHolder holder, SyncedDataKey<E, T> key)
    {
        this.holder = holder;
        this.key = key;
        this.value = key.defaultValueSupplier().get();
        this.signal = new SyncSignal(this::markForSync);
        this.updateSignal(this.value);
    }

    SyncedDataKey<E, T> getKey()
    {
        return this.key;
    }

    T getValue()
    {
        return this.value;
    }

    void setValue(T value)
    {
        this.updateSignal(value);
        this.value = value;
        this.markForSync();
    }

    public void markForSync()
    {
        if(this.key.syncMode().willSync())
        {
            if(this.holder != null && this.holder.markForSync())
            {
                this.pendingSync = true;
            }
        }
    }

    boolean isPendingSync()
    {
        return this.pendingSync;
    }

    void clearSync()
    {
        this.pendingSync = false;
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
        DataEntry<?, ?> entry = new DataEntry<>(null, key);
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

    private void updateSignal(T value)
    {
        if(value instanceof SyncSignal.Consumer consumer)
        {
            consumer.accept(this.signal);
        }
    }
}
