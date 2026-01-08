package com.mrcrayfish.framework.entity.sync;

import com.mrcrayfish.framework.api.sync.SyncSignal;
import com.mrcrayfish.framework.api.sync.SyncedDataKey;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.apache.commons.lang3.Validate;

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
        this.key.serializer().streamCodec().encode(buffer, this.value);
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
        this.value = this.getKey().serializer().streamCodec().decode(buffer);
    }

    public void write(ValueOutput output)
    {
        this.key.serializer().writer().accept(output, this.value);
    }

    public void read(ValueInput input)
    {
        this.removeSignal();
        T newValue = this.key.serializer().reader().apply(input);
        if(newValue != null)
        {
            this.value = newValue;
        }
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
