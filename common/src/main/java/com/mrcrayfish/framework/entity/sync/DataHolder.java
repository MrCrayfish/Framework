package com.mrcrayfish.framework.entity.sync;

import com.mrcrayfish.framework.Constants;
import com.mrcrayfish.framework.api.sync.SyncedClassKey;
import com.mrcrayfish.framework.api.sync.SyncedDataKey;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Author: MrCrayfish
 */
public class DataHolder
{
    public static final DataHolder EMPTY = new DataHolder.Empty();

    private final Entity entity;
    private Map<SyncedDataKey<?, ?>, DataEntry<?, ?>> dataMap = new HashMap<>();
    private boolean pendingSync;

    public DataHolder(Entity entity)
    {
        this.entity = entity;
    }

    @SuppressWarnings("unchecked")
    <E extends Entity, T> boolean set(SyncedDataKey<?, ?> key, T value)
    {
        DataEntry<E, T> entry = (DataEntry<E, T>) this.dataMap.computeIfAbsent(key, key2 -> new DataEntry<>(this, key2));
        if(!entry.getValue().equals(value))
        {
            entry.setValue(value);
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

    boolean markForSync()
    {
        if(SyncedEntityData.instance().markForSync(this.entity))
        {
            this.pendingSync = true;
            return true;
        }
        return false;
    }

    boolean isPendingSync()
    {
        return this.pendingSync;
    }

    void clearSync()
    {
        this.pendingSync = false;
        this.dataMap.forEach((key, entry) -> entry.clearSync());
    }

    List<DataEntry<?, ?>> gatherPendingSyncDataEntries()
    {
        return this.dataMap.values().stream().filter(DataEntry::isPendingSync).filter(entry -> entry.getKey().syncMode().willSync()).collect(Collectors.toList());
    }

    List<DataEntry<?, ?>> gatherAllTrackingDataEntries()
    {
        return this.dataMap.values().stream().filter(entry -> entry.getKey().syncMode().willSync()).collect(Collectors.toList());
    }

    public boolean serialize(ValueOutput output)
    {
        ValueOutput.ValueOutputList list = output.childrenList("Entries");
        this.dataMap.forEach((key, entry) -> {
            if(key.save()) {
                ValueOutput entryOutput = list.addChild();
                entryOutput.putString("ClassKey", key.classKey().id().toString());
                entryOutput.putString("DataKey", key.id().toString());
                entry.write(entryOutput);
            }
        });
        return true;
    }

    public void deserialize(ValueInput input)
    {
        this.dataMap.clear();
        ValueInput.ValueInputList list = input.childrenListOrEmpty("Entries");
        list.forEach(entryInput ->
        {
            Optional<String> rawClassKey = entryInput.getString("ClassKey");
            if(rawClassKey.isEmpty())
                return;

            Optional<String> rawDataKey = entryInput.getString("DataKey");
            if(rawDataKey.isEmpty())
                return;

            ResourceLocation classKey = ResourceLocation.tryParse(rawClassKey.get());
            SyncedClassKey<?> syncedClassKey = SyncedEntityData.instance().getClassKey(classKey);
            if(syncedClassKey == null)
                return;

            Map<ResourceLocation, SyncedDataKey<?, ?>> keys = SyncedEntityData.instance().getDataKeys(syncedClassKey);
            if(keys == null)
                return;

            ResourceLocation dataKey = ResourceLocation.tryParse(rawDataKey.get());
            SyncedDataKey<?, ?> syncedDataKey = keys.get(dataKey);
            if(syncedDataKey == null || !syncedDataKey.save())
                return;

            DataEntry<?, ?> entry = new DataEntry<>(this, syncedDataKey);
            entry.read(entryInput);
            this.dataMap.put(syncedDataKey, entry);
        });
    }

    public void copyInto(DataHolder other, HolderLookup.Provider provider, boolean copyAllKeys)
    {
        try(ProblemReporter.ScopedCollector collector = new ProblemReporter.ScopedCollector(this.entity.problemPath(), Constants.LOG))
        {
            Map<SyncedDataKey<?, ?>, DataEntry<?, ?>> newDataMap = new HashMap<>();
            this.dataMap.forEach((key, entry) ->
            {
                if(copyAllKeys || key.persistent())
                {
                    DataEntry<?, ?> newEntry = new DataEntry<>(other, key);
                    TagValueOutput output = TagValueOutput.createWithContext(collector, provider);
                    entry.write(output);
                    ValueInput input = TagValueInput.create(collector, provider, output.buildResult());
                    newEntry.read(input);
                    newDataMap.put(key, newEntry);
                }
            });
            other.dataMap = newDataMap;
        }
    }

    private static class Empty extends DataHolder
    {
        public Empty()
        {
            super(null);
        }

        @Override
        <E extends Entity, T> boolean set(SyncedDataKey<?, ?> key, T value)
        {
            return false;
        }

        @Override
        <E extends Entity, T> @Nullable T get(SyncedDataKey<E, T> key)
        {
            return null;
        }

        @Override
        boolean markForSync()
        {
            return false;
        }

        @Override
        boolean isPendingSync()
        {
            return false;
        }

        @Override
        void clearSync() {}

        @Override
        List<DataEntry<?, ?>> gatherAllTrackingDataEntries()
        {
            return Collections.emptyList();
        }

        @Override
        List<DataEntry<?, ?>> gatherPendingSyncDataEntries()
        {
            return Collections.emptyList();
        }

        @Override
        public boolean serialize(ValueOutput output)
        {
            return false;
        }

        @Override
        public void deserialize(ValueInput input) {}

        @Override
        public void copyInto(DataHolder other, HolderLookup.Provider provider, boolean copyAllKeys) {}
    }
}
