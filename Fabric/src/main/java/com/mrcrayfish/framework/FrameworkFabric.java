package com.mrcrayfish.framework;

import com.mrcrayfish.framework.api.registry.BlockRegistryEntry;
import com.mrcrayfish.framework.api.registry.IRegisterFunction;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class FrameworkFabric implements ModInitializer
{
    public FrameworkFabric()
    {
        Bootstrap.earlyInit();

        // Register all entries
        Registration.getAllRegistryEntries().forEach(entry ->
        {
            entry.register(new IRegisterFunction()
            {
                @Override
                public <T> void call(Registry<T> registry, ResourceLocation name, Supplier<T> supplier)
                {
                    Registry.register(registry, name, supplier.get());
                }
            });
        });

        // Special case for block registry entries to register items
        Registration.get(Registry.BLOCK_REGISTRY).forEach(entry ->
        {
            if(entry instanceof BlockRegistryEntry<?, ?> blockEntry)
            {
                blockEntry.item().ifPresent(item -> Registry.register(Registry.ITEM, entry.getId(), item));
            }
        });
    }

    @Override
    public void onInitialize()
    {
        Bootstrap.init();
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            FrameworkData.setLoaded();
        });
    }
}
