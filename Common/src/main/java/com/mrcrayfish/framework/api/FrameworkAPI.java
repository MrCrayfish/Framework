package com.mrcrayfish.framework.api;

import com.mrcrayfish.framework.api.data.login.ILoginData;
import com.mrcrayfish.framework.api.network.FrameworkNetworkBuilder;
import com.mrcrayfish.framework.network.LoginDataManager;
import com.mrcrayfish.framework.platform.Services;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class FrameworkAPI
{
    public static void registerLoginData(ResourceLocation id, Supplier<ILoginData> supplier)
    {
        LoginDataManager.registerLoginData(id, supplier);
    }

    public static FrameworkNetworkBuilder createNetworkBuilder(ResourceLocation id, int version)
    {
        return Services.NETWORK.createNetworkBuilder(id, version);
    }
}
