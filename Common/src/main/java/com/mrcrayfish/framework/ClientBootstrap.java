package com.mrcrayfish.framework;

import com.mrcrayfish.framework.api.Environment;
import com.mrcrayfish.framework.api.util.EnvironmentHelper;
import net.minecraft.client.Minecraft;

/**
 * Author: MrCrayfish
 */
public class ClientBootstrap
{
    public static void init()
    {
        EnvironmentHelper.setExecutor(Environment.CLIENT, Minecraft.getInstance());
    }
}
