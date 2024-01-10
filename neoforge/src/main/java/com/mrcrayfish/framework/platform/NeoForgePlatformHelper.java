package com.mrcrayfish.framework.platform;

import com.mrcrayfish.framework.api.Environment;
import com.mrcrayfish.framework.platform.services.IPlatformHelper;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;

public class NeoForgePlatformHelper implements IPlatformHelper
{
    @Override
    public Platform getPlatform()
    {
        return Platform.FORGE;
    }

    @Override
    public boolean isModLoaded(String modId)
    {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public String getModName(String modId)
    {
        return ModList.get().getModContainerById(modId).map(container -> container.getModInfo().getDisplayName()).orElse("Unknown");
    }

    @Override
    public boolean isDevelopmentEnvironment()
    {
        return !FMLLoader.isProduction();
    }

    @Override
    public Environment getEnvironment()
    {
        return FMLLoader.getDist().isClient() ? Environment.CLIENT : Environment.DEDICATED_SERVER;
    }
}