package com.mrcrayfish.framework;

import com.mrcrayfish.framework.config.ConfigWatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

/**
 * Author: MrCrayfish
 */
public class FrameworkFabric implements ModInitializer
{
    public FrameworkFabric()
    {
        FrameworkSetup.run();
    }

    @Override
    public void onInitialize()
    {
        FrameworkSetup.init();
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            FrameworkData.setLoaded();
        });
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            if(server.isDedicatedServer()) {
                ConfigWatcher.get().stop();
            }
        });
    }
}
