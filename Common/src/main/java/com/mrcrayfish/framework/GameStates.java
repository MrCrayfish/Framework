package com.mrcrayfish.framework;

/**
 * Author: MrCrayfish
 */
public class GameStates
{
    private static boolean gameLoaded;

    public static void setLoaded()
    {
        gameLoaded = true;
    }

    public static boolean isLoaded()
    {
        return gameLoaded;
    }
}
