package com.mrcrayfish.framework.api;

/**
 * Author: MrCrayfish
 */
public enum Environment
{
    CLIENT(LogicalEnvironment.CLIENT),
    DEDICATED_SERVER(LogicalEnvironment.SERVER);

    final LogicalEnvironment logical;

    Environment(LogicalEnvironment logical)
    {
        this.logical = logical;
    }

    public LogicalEnvironment getLogical()
    {
        return this.logical;
    }

    public boolean isClient()
    {
        return this == CLIENT;
    }

    public boolean isDedicatedServer()
    {
        return this == DEDICATED_SERVER;
    }
}
