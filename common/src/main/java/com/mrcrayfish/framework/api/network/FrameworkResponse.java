package com.mrcrayfish.framework.api.network;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public record FrameworkResponse(boolean state, @Nullable String message)
{
    public static final FrameworkResponse SUCCESS = FrameworkResponse.success();

    public boolean isError()
    {
        return !this.state;
    }

    public static FrameworkResponse success()
    {
        return new FrameworkResponse(true, null);
    }

    public static FrameworkResponse error(String message)
    {
        return new FrameworkResponse(false, message);
    }
}
