package com.mrcrayfish.framework;

import com.mrcrayfish.framework.api.LogicalEnvironment;
import com.mrcrayfish.framework.api.event.ServerEvents;
import com.mrcrayfish.framework.api.registry.BlockRegistryEntry;
import com.mrcrayfish.framework.api.registry.IRegisterFunction;
import com.mrcrayfish.framework.api.util.EnvironmentHelper;
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
        if(!initialized)
        {
            Registration.init();
            FrameworkConfigManager.getInstance();
            initialized = true;
        }
    }

    static void init()
    {
        Network.init();
        ServerEvents.STARTED.register(server -> {
            EnvironmentHelper.setExecutor(LogicalEnvironment.SERVER, server);
        });
        ServerEvents.STOPPING.register(server -> {
            EnvironmentHelper.setExecutor(LogicalEnvironment.SERVER, null);
        });
    }
}
