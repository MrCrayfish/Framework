package com.mrcrayfish.framework;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class SideExecutor
{
    public static void runOn(EnvType type, Supplier<Runnable> supplier)
    {
        if(type == FabricLoader.getInstance().getEnvironmentType()) supplier.get().run();
    }
}
