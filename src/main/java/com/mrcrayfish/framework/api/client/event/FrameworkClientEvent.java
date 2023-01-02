package com.mrcrayfish.framework.api.client.event;

import com.mrcrayfish.framework.api.client.resources.IDataLoader;
import com.mrcrayfish.framework.api.client.resources.IResourceSupplier;
import com.mrcrayfish.framework.client.JsonDataManager;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

/**
 * Author: MrCrayfish
 */
public sealed class FrameworkClientEvent extends Event permits FrameworkClientEvent.Register
{
    /**
     * Simple client event for registration. This event is executed on the mod bus.
     */
    public static final class Register extends FrameworkClientEvent implements IModBusEvent
    {
        /**
         * Registers a data loader.
         *
         * @param loader the data loader instance
         */
        public <T extends IResourceSupplier> void registerDataLoader(IDataLoader<T> loader)
        {
            // Internal code, do not call these directly since they may break in a future update.
            JsonDataManager.getInstance().addLoader(loader);
        }
    }
}
