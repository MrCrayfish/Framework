package com.mrcrayfish.framework.api.event;

import com.mrcrayfish.framework.api.data.login.ILoginData;
import com.mrcrayfish.framework.api.data.sync.SyncedDataKey;
import com.mrcrayfish.framework.common.data.SyncedEntityData;
import com.mrcrayfish.framework.network.Network;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

import java.util.function.Supplier;

/**
 * Framework's main event class for developers to interact with Framework's systems.
 *
 * Author: MrCrayfish
 */
public sealed class FrameworkEvent extends Event permits FrameworkEvent.Register
{
    public static final class Register extends FrameworkEvent implements IModBusEvent
    {
        /**
         * Registers a synced data key into Framework's system. This should be called during common setup.
         * An exception will be thrown if this is called after the initialization phase of the game.
         * See {@link SyncedDataKey} for more information.
         *
         * @param key the synced data key instance
         */
        public <E extends Entity, T> void registerSyncedDataKey(SyncedDataKey<E, T> key)
        {
            // Internal code, do not call these directly since they may break in a future update.
            SyncedEntityData.instance().registerDataKey(key);
        }

        /**
         * Registers custom login data into Framework's system. This should be called during common setup.
         * An exception will be thrown if this is called after the initialization phase of the game.
         * See {@link ILoginData} for more information.
         *
         * @param id   the id to represent the data
         * @param data a supplier returning a login data instance
         */
        public void registerLoginData(ResourceLocation id, Supplier<ILoginData> data)
        {
            // Internal code, do not call these directly since they may break in a future update.
            Network.registerLoginData(id, data);
        }
    }
}
