package com.mrcrayfish.framework.entity.sync;

import com.mrcrayfish.framework.api.sync.SyncSignal;
import com.mrcrayfish.framework.api.sync.SyncedDataKey;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
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

    DataEntry(@Nullable DataHolder holder, SyncedDataKey<E, T> key)
    {
        this.holder = holder;
        this.key = key;
        this.value = key.defaultValueSupplier().apply(new Updatable(this));
        this.signal = new SyncSignal(this::markForSync);
        this.updateSignal();
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
        this.removeSignal();
        this.value = value;
        this.updateSignal();
        this.markForSync();
    }

    public void markForSync()
    {
        if(this.holder != null && this.holder.canSync() && this.key.syncMode().willSync())
        {
            this.pendingSync = true;
            this.holder.markForSync();
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

    public void write(FriendlyByteBuf buffer)
    {
        int id = SyncedEntityData.instance().getInternalId(this.key);
        buffer.writeVarInt(id);
        this.key.serializer().write(buffer, this.value);
    }

    public static DataEntry<?, ?> createClientEntry(FriendlyByteBuf buffer)
    {
        SyncedDataKey<?, ?> key = SyncedEntityData.instance().getKey(buffer.readVarInt());
        Validate.notNull(key, "Synced key does not exist for id");
        DataEntry<?, ?> entry = new DataEntry<>(null, key);
        entry.readValue(buffer);
        return entry;
    }

    private void readValue(FriendlyByteBuf buffer)
    {
        this.value = this.getKey().serializer().read(Updatable.NULL, buffer);
    }

    Tag writeValue()
    {
        return this.key.serializer().write(this.value);
    }

    void readValue(Tag nbt)
    {
        this.removeSignal();
        this.value = this.key.serializer().read(new Updatable(this), nbt);
        this.updateSignal();
    }

    private void updateSignal()
    {
        if(this.value instanceof SyncSignal.Consumer consumer)
        {
            consumer.accept(this.signal);
        }
    }

    private void removeSignal()
    {
        if(this.value instanceof SyncSignal.Consumer consumer)
        {
            consumer.accept(null);
        }
    }
}
