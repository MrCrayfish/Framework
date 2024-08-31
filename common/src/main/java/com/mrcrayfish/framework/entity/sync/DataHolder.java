package com.mrcrayfish.framework.entity.sync;

import com.mrcrayfish.framework.api.sync.SyncedClassKey;
import com.mrcrayfish.framework.api.sync.SyncedDataKey;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import org.jetbrains.annotations.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Author: MrCrayfish
 */
public class DataHolder
{
    public static final DataHolder UNIVERSAL = new DataHolder();

    Map<SyncedDataKey<?, ?>, DataEntry<?, ?>> dataMap = new HashMap<>();
    private boolean dirty = false;

    @SuppressWarnings("unchecked")
    <E extends Entity, T> boolean set(E entity, SyncedDataKey<?, ?> key, T value)
    {
        DataEntry<E, T> entry = (DataEntry<E, T>) this.dataMap.computeIfAbsent(key, key2 -> new DataEntry<>(this, key2));
        if(!entry.getValue().equals(value))
        {
            boolean dirty = !entity.level().isClientSide() && entry.getKey().syncMode() != SyncedDataKey.SyncMode.NONE;
            entry.setValue(value, dirty);
            return true;
        }
        return false;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    <E extends Entity, T> T get(SyncedDataKey<E, T> key)
    {
        return (T) this.dataMap.computeIfAbsent(key, key2 -> new DataEntry<>(this, key2)).getValue();
    }

    public void markDirty()
    {
        this.dirty = true;
        if(this != UNIVERSAL)
        {
            SyncedEntityData.instance().markDirty();
        }
    }

    public boolean isDirty()
    {
        return this.dirty;
    }

    void clean()
    {
        this.dirty = false;
        this.dataMap.forEach((key, entry) -> entry.clean());
    }

    List<DataEntry<?, ?>> gatherDirty()
    {
        return this.dataMap.values().stream().filter(DataEntry::isDirty).filter(entry -> entry.getKey().syncMode() != SyncedDataKey.SyncMode.NONE).collect(Collectors.toList());
    }

    List<DataEntry<?, ?>> gatherAll()
    {
        return this.dataMap.values().stream().filter(entry -> entry.getKey().syncMode() != SyncedDataKey.SyncMode.NONE).collect(Collectors.toList());
    }

    public ListTag serialize()
    {
        ListTag list = new ListTag();
        this.dataMap.forEach((key, entry) ->
        {
            if(key.save())
            {
                CompoundTag keyTag = new CompoundTag();
                keyTag.putString("ClassKey", key.classKey().id().toString());
                keyTag.putString("DataKey", key.id().toString());
                keyTag.put("Value", entry.writeValue());
                list.add(keyTag);
            }
        });
        return list;
    }

    public void deserialize(ListTag listTag)
    {
        this.dataMap.clear();
        listTag.forEach(entryTag ->
        {
            CompoundTag keyTag = (CompoundTag) entryTag;
            ResourceLocation classKey = ResourceLocation.tryParse(keyTag.getString("ClassKey"));
            ResourceLocation dataKey = ResourceLocation.tryParse(keyTag.getString("DataKey"));
            Tag value = keyTag.get("Value");

            SyncedClassKey<?> syncedClassKey = SyncedEntityData.instance().getClassKey(classKey);
            if(syncedClassKey == null)
                return;

            Map<ResourceLocation, SyncedDataKey<?, ?>> keys = SyncedEntityData.instance().getDataKeys(syncedClassKey);
            if(keys == null)
                return;

            SyncedDataKey<?, ?> syncedDataKey = keys.get(dataKey);
            if(syncedDataKey == null || !syncedDataKey.save())
                return;

            DataEntry<?, ?> entry = new DataEntry<>(this, syncedDataKey);
            entry.readValue(value);
            this.dataMap.put(syncedDataKey, entry);
        });
    }
}
