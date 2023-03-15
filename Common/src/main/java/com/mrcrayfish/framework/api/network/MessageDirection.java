package com.mrcrayfish.framework.api.network;

/**
 * Author: MrCrayfish
 */
public enum MessageDirection
{
    PLAY_SERVER_BOUND(false, true),
    PLAY_CLIENT_BOUND(true, false),
    LOGIN_SERVER_BOUND(false, true),
    LOGIN_CLIENT_BOUND(true, false);

    private final boolean client;
    private final boolean server;

    MessageDirection(boolean client, boolean server)
    {
        this.client = client;
        this.server = server;
    }

    public boolean isClient()
    {
        return this.client;
    }

    public boolean isServer()
    {
        return this.server;
    }
}
