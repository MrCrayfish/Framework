package com.mrcrayfish.framework.api.sync;

import org.jetbrains.annotations.Nullable;

/**
 * Author: MrCrayfish
 */
public abstract class SyncedObject implements SyncSignal.Consumer
{
    @Nullable
    private SyncSignal signal;

    @Override
    public void accept(SyncSignal signal)
    {
        this.signal = signal;
    }

    protected final void markDirty()
    {
        if(this.signal != null)
        {
            this.signal.tell();
        }
    }
}
