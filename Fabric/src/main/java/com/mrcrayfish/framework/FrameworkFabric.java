package com.mrcrayfish.framework;

import com.mrcrayfish.framework.api.registry.IRegisterFunction;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class FrameworkFabric implements ModInitializer
{
    public FrameworkFabric()
    {
        Registration.init();
    }

    @Override
    public void onInitialize()
    {
        Bootstrap.init();
        Registration.getAllRegistryEntries().forEach(entry ->
        {
            entry.register(new IRegisterFunction()
            {
                @Override
                public <T> void call(Registry<T> registry, ResourceLocation name, Supplier<T> valueSupplier)
                {
                    Registry.register(registry, name, valueSupplier.get());
                }
            });
        });

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            FrameworkData.setLoaded();
        });
    }
}
