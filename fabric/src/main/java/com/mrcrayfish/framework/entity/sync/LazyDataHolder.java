package com.mrcrayfish.framework.entity.sync;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;

/**
 * Author: MrCrayfish
 */
public class LazyDataHolder
{
    private final CompoundTag data;
    private final Entity entity;
    private DataHolder holder;

    public LazyDataHolder(CompoundTag data, Entity entity)
    {
        this.data = data;
        this.entity = entity;
    }

    public DataHolder get()
    {
        if(this.holder == null)
        {
            this.holder = this.create();
        }
        return this.holder;
    }

    private DataHolder create()
    {
        DataHolder newHolder = new DataHolder();
        newHolder.deserialize(this.data.getList("Keys", Tag.TAG_COMPOUND), this.entity.registryAccess());
        newHolder.setup(this.entity);
        return newHolder;
    }

    public CompoundTag serialize()
    {
        if(this.holder != null)
        {
            CompoundTag tag = new CompoundTag();
            tag.put("Keys", this.holder.serialize(this.entity.registryAccess()));
            return tag;
        }
        return this.data;
    }
}
