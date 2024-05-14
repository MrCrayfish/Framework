package com.mrcrayfish.framework.entity.sync;

/**
 * Author: MrCrayfish
 */
public class Updatable
{
    private final DataEntry<?, ?> entry;

    Updatable(DataEntry<?, ?> entry)
    {
        this.entry = entry;
    }

    /**
     * Marks the holding object as dirty.
     */
    public void markDirty()
    {
        if(this.entry != null)
        {
            this.entry.markDirty();
        }
    }
}
