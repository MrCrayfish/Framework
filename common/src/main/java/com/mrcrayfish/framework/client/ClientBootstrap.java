package com.mrcrayfish.framework.client;

import com.mrcrayfish.framework.api.LogicalEnvironment;
import com.mrcrayfish.framework.api.util.TaskRunner;
import net.minecraft.client.Minecraft;

/**
 * Author: MrCrayfish
 */
public class ClientBootstrap
{
    public static void init()
    {
        TaskRunner.setExecutor(LogicalEnvironment.CLIENT, Minecraft.getInstance());
    }
}
