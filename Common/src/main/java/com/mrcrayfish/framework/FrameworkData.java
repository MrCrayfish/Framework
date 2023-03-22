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
}
