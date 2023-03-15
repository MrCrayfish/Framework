package com.mrcrayfish.framework.platform;

import com.mrcrayfish.framework.platform.network.ForgeNetworkBuilder;
import com.mrcrayfish.framework.api.network.FrameworkNetworkBuilder;
import com.mrcrayfish.framework.platform.services.INetworkHelper;
import net.minecraft.resources.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public class ForgeNetworkHelper implements INetworkHelper
{
    @Override
    public FrameworkNetworkBuilder createNetworkBuilder(ResourceLocation id, int version)
    {
        return new ForgeNetworkBuilder(id, version);
    }


}
