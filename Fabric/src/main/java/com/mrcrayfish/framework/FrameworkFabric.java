package com.mrcrayfish.framework;

import com.mrcrayfish.framework.api.registry.BlockRegistryEntry;
import com.mrcrayfish.framework.api.registry.IRegisterFunction;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
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
                public <T> void call(Registry<T> registry, ResourceLocation name, Supplier<T> supplier)
                {
                    Registry.register(registry, name, supplier.get());
                }
            });
        });

        // Special case for block registry entries to register items
        Registration.get(Registries.BLOCK).forEach(entry ->
        {
            if(entry instanceof BlockRegistryEntry<?, ?> blockEntry)
            {
                blockEntry.item().ifPresent(item -> Registry.register(BuiltInRegistries.ITEM, entry.getId(), item));
            }
        });

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            FrameworkData.setLoaded();
        });
    }
}
