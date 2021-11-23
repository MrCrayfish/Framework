package com.mrcrayfish.framework.api;

import com.mrcrayfish.framework.api.data.SyncedDataKey;
import com.mrcrayfish.framework.common.data.SyncedPlayerData;

/**
 * Author: MrCrayfish
 */
public class FrameworkAPI
{
    public static void registerSyncedDataKey(SyncedDataKey<?> key)
    {
        // Internal code, do not call these directly since they may break in a future update.
        SyncedPlayerData.instance().registerKey(key);
    }
}
