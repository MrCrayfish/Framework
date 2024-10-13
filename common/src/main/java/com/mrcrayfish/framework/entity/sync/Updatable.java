package com.mrcrayfish.framework.entity.sync;

/**
 * Author: MrCrayfish
 */
// Removed in favour of SyncedObject and SyncSignal
@Deprecated(forRemoval = true, since = "0.7.9")
public class Updatable
{
    public static final Updatable NULL = new Updatable(null);

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
            this.entry.markForSync();
        }
    }
}
