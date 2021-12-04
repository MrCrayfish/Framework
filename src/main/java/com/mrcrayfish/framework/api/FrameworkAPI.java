package com.mrcrayfish.framework.api;

import com.mrcrayfish.framework.api.data.login.ILoginData;
import com.mrcrayfish.framework.api.data.sync.SyncedDataKey;
import com.mrcrayfish.framework.common.data.SyncedEntityData;
import com.mrcrayfish.framework.network.Network;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.function.Supplier;

/**
 * Framework's main API class for developers to interact with Framework's systems.
 *
 * Author: MrCrayfish
 */
public class FrameworkAPI
{
    /**
     * Registers a synced data key into Framework's system. This can only be called during the
     * initialization phase of the game, otherwise an exception will be thrown. See {@link SyncedDataKey}
     * for more information.
     *
     * @param key the synced data key instance
     */
    public static <E extends Entity, T> void registerSyncedDataKey(SyncedDataKey<E, T> key)
    {
        // Internal code, do not call these directly since they may break in a future update.
        SyncedEntityData.instance().registerDataKey(key);
    }

    /**
     * Registers custom login data into Framework's system. This can only be called during the
     * initialization phase of the game, otherwise an exception will be thrown. See {@link ILoginData}
     * for more information.
     *
     * @param id   the id to represent the data
     * @param data a supplier returning a login data instance
     */
    public static void registerLoginData(ResourceLocation id, Supplier<ILoginData> data)
    {
        // Internal code, do not call these directly since they may break in a future update.
        Network.registerLoginData(id, data);
    }
}
