package com.mrcrayfish.framework;

import com.mrcrayfish.framework.api.Environment;

/**
 * Author: MrCrayfish
 */
public class FrameworkData
{
    private static boolean gameLoaded;
    private static Environment env;

    public static void setLoaded()
    {
        gameLoaded = true;
    }

    public static boolean isLoaded()
    {
        return gameLoaded;
    }

    public static void setEnvironment(Environment env)
    {
        if(FrameworkData.env == null)
        {
            FrameworkData.env = env;
        }
    }

    public static Environment getEnvironment()
    {
        return env;
    }
}
