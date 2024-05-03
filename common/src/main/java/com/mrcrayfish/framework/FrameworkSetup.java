package com.mrcrayfish.framework;

import com.mrcrayfish.framework.api.LogicalEnvironment;
import com.mrcrayfish.framework.api.event.ClientEvents;
import com.mrcrayfish.framework.api.event.ServerEvents;
import com.mrcrayfish.framework.api.registry.BlockRegistryEntry;
import com.mrcrayfish.framework.api.registry.IRegisterFunction;
import com.mrcrayfish.framework.api.util.TaskRunner;
import com.mrcrayfish.framework.config.FrameworkConfigManager;
import com.mrcrayfish.framework.network.Network;
import com.mrcrayfish.framework.platform.Services;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class FrameworkSetup
{
    private static boolean initialized;

    public static void run()
    {
        if(initialized)
            return;

        Registration.init();
        FrameworkConfigManager.getInstance();

        if(Services.PLATFORM.getPlatform().isFabric())
        {
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
            Registration.get(Registries.BLOCK).forEach(entry ->
            {
                if(entry instanceof BlockRegistryEntry<?, ?> blockEntry)
                {
                    blockEntry.item().ifPresent(item -> Registry.register(BuiltInRegistries.ITEM, entry.getId(), item));
                }
            });
        }
        initialized = true;
    }

    static void init()
    {
        Network.init();
        ServerEvents.STARTED.register(server -> {
            TaskRunner.setExecutor(LogicalEnvironment.SERVER, server);
        });
        ServerEvents.STOPPED.register(server -> {
            TaskRunner.setExecutor(LogicalEnvironment.SERVER, null);
        });
    }
}
