package com.mrcrayfish.framework;

import com.mrcrayfish.framework.api.event.EntityEvents;
import com.mrcrayfish.framework.config.ConfigWatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
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
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            EntityEvents.LIVING_ENTITY_DEATH.post().handle(entity, damageSource);
        });
    }
}
