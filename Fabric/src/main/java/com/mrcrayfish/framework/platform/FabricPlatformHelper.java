package com.mrcrayfish.framework.platform;

import com.mrcrayfish.framework.platform.services.IPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;

public class FabricPlatformHelper implements IPlatformHelper
{
    @Override
    public String getPlatformName()
    {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId)
    {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public String getModName(String modId)
    {
        return FabricLoader.getInstance().getModContainer(modId).map(container -> container.getMetadata().getName()).orElse("Unknown");
    }

    @Override
    public boolean isDevelopmentEnvironment()
    {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }
}
