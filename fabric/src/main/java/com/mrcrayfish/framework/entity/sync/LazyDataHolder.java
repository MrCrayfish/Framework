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
    private DataHolder holder;

    public LazyDataHolder(CompoundTag data)
    {
        this.data = data;
    }

    public DataHolder get(Entity entity)
    {
        if(this.holder == null)
        {
            this.holder = this.create(entity);
        }
        return this.holder;
    }

    private DataHolder create(Entity entity)
    {
        DataHolder newHolder = new DataHolder();
        newHolder.setup(entity);
        newHolder.deserialize(this.data.getList("Keys", Tag.TAG_COMPOUND));
        return newHolder;
    }

    public CompoundTag serialize()
    {
        if(this.holder != null)
        {
            CompoundTag tag = new CompoundTag();
            tag.put("Keys", this.holder.serialize());
            return tag;
        }
        return this.data;
    }
}
