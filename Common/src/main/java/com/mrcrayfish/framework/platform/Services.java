package com.mrcrayfish.framework.platform;

import com.mrcrayfish.framework.Constants;
import com.mrcrayfish.framework.platform.services.IClientHelper;
import com.mrcrayfish.framework.platform.services.INetworkHelper;
import com.mrcrayfish.framework.platform.services.IPlatformHelper;

import java.util.ServiceLoader;

public class Services
{
    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);
    public static final INetworkHelper NETWORK = load(INetworkHelper.class);
    public static final IClientHelper CLIENT = load(IClientHelper.class);

    public static <T> T load(Class<T> clazz)
    {
        final T loadedService = ServiceLoader.load(clazz).findFirst().orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        Constants.LOG.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}