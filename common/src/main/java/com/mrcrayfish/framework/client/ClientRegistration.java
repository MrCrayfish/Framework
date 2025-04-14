package com.mrcrayfish.framework.client;

import com.mrcrayfish.framework.api.client.model.FrameworkModelResource;
import com.mrcrayfish.framework.client.model.StandaloneModelManager;
import com.mrcrayfish.framework.platform.Services;
import net.minecraft.client.KeyMapping;

public class ClientRegistration
{
    public static void init()
    {
        Services.REGISTRATION.getRegistryObjects(FrameworkModelResource.class).forEach(resource -> {
            StandaloneModelManager.getInstance().register((FrameworkModelResource<?>) resource);
        });
    }
}
