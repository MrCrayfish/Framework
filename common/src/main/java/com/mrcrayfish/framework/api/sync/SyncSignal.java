package com.mrcrayfish.framework.api.sync;

import org.jetbrains.annotations.Nullable;

/**
 * Author: MrCrayfish
 */
public final class SyncSignal
{
    private final Runnable markDirty;

    public SyncSignal(Runnable markDirty)
    {
        this.markDirty = markDirty;
    }

    public void tell()
    {
        this.markDirty.run();
    }

    public interface Consumer
    {
        void accept(@Nullable SyncSignal signal);
    }
}