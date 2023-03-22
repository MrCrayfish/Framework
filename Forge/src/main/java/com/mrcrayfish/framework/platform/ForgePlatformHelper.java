package com.mrcrayfish.framework.platform;

import com.mrcrayfish.framework.api.Environment;
import com.mrcrayfish.framework.platform.services.IPlatformHelper;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;

public class ForgePlatformHelper implements IPlatformHelper
{
    @Override
    public String getPlatformName()
    {
        return "Forge";
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