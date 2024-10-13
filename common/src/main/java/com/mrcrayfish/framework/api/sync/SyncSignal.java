package com.mrcrayfish.framework.api.sync;

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
        void accept(SyncSignal signal);
    }
}