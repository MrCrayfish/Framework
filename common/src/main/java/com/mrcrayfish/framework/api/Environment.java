package com.mrcrayfish.framework.api;

/**
 * Author: MrCrayfish
 */
public enum Environment
{
    CLIENT,
    DEDICATED_SERVER;

    public boolean isClient()
    {
        return this == CLIENT;
    }

    public boolean isDedicatedServer()
    {
        return this == DEDICATED_SERVER;
    }
}
