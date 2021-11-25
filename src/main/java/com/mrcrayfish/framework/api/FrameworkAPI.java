package com.mrcrayfish.framework.api;

import com.mrcrayfish.framework.api.data.login.ILoginData;
import com.mrcrayfish.framework.api.data.sync.SyncedDataKey;
import com.mrcrayfish.framework.common.data.SyncedPlayerData;
import com.mrcrayfish.framework.network.Network;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class FrameworkAPI
{
    /**
     *
     * @param key
     */
    public static void registerSyncedDataKey(SyncedDataKey<?> key)
    {
        // Internal code, do not call these directly since they may break in a future update.
        SyncedPlayerData.instance().registerKey(key);
    }

    /**
     *
     * @param id
     * @param data
     */
    public static void registerLoginData(ResourceLocation id, Supplier<ILoginData> data)
    {
        // Internal code, do not call these directly since they may break in a future update.
        Network.registerLoginData(id, data);
    }
}
