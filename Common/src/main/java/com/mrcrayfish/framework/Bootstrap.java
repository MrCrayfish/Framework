package com.mrcrayfish.framework;

import com.mrcrayfish.framework.api.Environment;
import com.mrcrayfish.framework.api.event.ServerEvents;
import com.mrcrayfish.framework.api.util.EnvironmentHelper;
import com.mrcrayfish.framework.network.Network;

/**
 * Author: MrCrayfish
 */
public class Bootstrap
{
    public static void init()
    {
        Network.init();
        ServerEvents.STARTED.register(server -> {
            EnvironmentHelper.setExecutor(Environment.DEDICATED_SERVER, server);
        });
        ServerEvents.STOPPING.register(server -> {
            EnvironmentHelper.setExecutor(Environment.DEDICATED_SERVER, null);
        });
    }
}
