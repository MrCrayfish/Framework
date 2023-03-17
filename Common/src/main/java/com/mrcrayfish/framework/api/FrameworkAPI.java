package com.mrcrayfish.framework.api;

import com.mrcrayfish.framework.api.data.login.ILoginData;
import com.mrcrayfish.framework.api.network.FrameworkNetworkBuilder;
import com.mrcrayfish.framework.api.sync.SyncedDataKey;
import com.mrcrayfish.framework.entity.sync.DataHolder;
import com.mrcrayfish.framework.entity.sync.SyncedEntityData;
import com.mrcrayfish.framework.network.LoginDataManager;
import com.mrcrayfish.framework.platform.Services;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class FrameworkAPI
{
    /**
     * Registers custom login data into Framework's system. This should be called during common setup.
     * An exception will be thrown if this is called after the initialization phase of the game.
     * See {@link ILoginData} for more information.
     *
     * @param id   the id to represent the data
     * @param supplier a supplier returning a login data instance
     */
    public static void registerLoginData(ResourceLocation id, Supplier<ILoginData> supplier)
    {
        // Internal code, do not call these directly since they may break in a future update.
        LoginDataManager.registerLoginData(id, supplier);
    }

    /**
     * Registers a synced data key into Framework's system. This should be called during common setup.
     * An exception will be thrown if this is called after the initialization phase of the game.
     * See {@link SyncedDataKey} for more information.
     *
     * @param key the synced data key instance
     */
    public static <E extends Entity, T> void registerSyncedDataKey(SyncedDataKey<E, T> key)
    {
        // Internal code, do not call these directly since they may break in a future update.
        SyncedEntityData.instance().registerDataKey(key);
    }

    public static FrameworkNetworkBuilder createNetworkBuilder(ResourceLocation id, int version)
    {
        // Internal code, do not call these directly since they may break in a future update.
        return Services.NETWORK.createNetworkBuilder(id, version);
    }
}
