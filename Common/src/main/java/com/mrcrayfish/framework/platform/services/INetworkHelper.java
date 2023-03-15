package com.mrcrayfish.framework.platform.services;

import com.mrcrayfish.framework.api.network.FrameworkNetworkBuilder;
import net.minecraft.resources.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public interface INetworkHelper
{
    /**
     * Creates a network builder for the respective platform
     *
     * @param id the id of the network
     * @param version the protocol version
     * @return a network builder instance
     */
    FrameworkNetworkBuilder createNetworkBuilder(ResourceLocation id, int version);
}
