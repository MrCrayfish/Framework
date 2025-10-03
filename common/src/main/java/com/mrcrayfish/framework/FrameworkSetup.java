package com.mrcrayfish.framework;

import com.mrcrayfish.framework.api.LogicalEnvironment;
import com.mrcrayfish.framework.api.event.FrameworkServerEvents;
import com.mrcrayfish.framework.api.util.TaskRunner;
import com.mrcrayfish.framework.config.FrameworkConfigManager;

/**
 * Author: MrCrayfish
 */
public class FrameworkSetup
{
    private static boolean initialized;

    public static void run()
    {
        if(!initialized)
        {
            Registration.init();
            FrameworkConfigManager.getInstance();
            initialized = true;
        }
    }

    static void init()
    {
        FrameworkServerEvents.STARTED.register(server -> {
            TaskRunner.setExecutor(LogicalEnvironment.SERVER, server);
        });
        FrameworkServerEvents.STOPPED.register(server -> {
            TaskRunner.setExecutor(LogicalEnvironment.SERVER, null);
        });
    }
}
