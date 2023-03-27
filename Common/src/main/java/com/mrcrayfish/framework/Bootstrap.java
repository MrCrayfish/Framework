package com.mrcrayfish.framework;

import com.mrcrayfish.framework.api.Environment;
import com.mrcrayfish.framework.api.LogicalEnvironment;
import com.mrcrayfish.framework.api.event.ServerEvents;
import com.mrcrayfish.framework.api.util.EnvironmentHelper;
import com.mrcrayfish.framework.config.FrameworkConfigManager;
import com.mrcrayfish.framework.network.Network;

/**
 * Author: MrCrayfish
 */
public class Bootstrap
{
    public static void init()
    {
        FrameworkConfigManager.getInstance();
        Network.init();
        ServerEvents.STARTED.register(server -> {
            EnvironmentHelper.setExecutor(LogicalEnvironment.SERVER, server);
        });
        ServerEvents.STOPPING.register(server -> {
            EnvironmentHelper.setExecutor(LogicalEnvironment.SERVER, null);
        });
    }
}
