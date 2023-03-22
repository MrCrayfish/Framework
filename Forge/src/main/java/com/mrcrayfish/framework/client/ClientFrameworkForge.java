package com.mrcrayfish.framework.client;

import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;

/**
 * Author: MrCrayfish
 */
public final class ClientFrameworkForge
{
    public static void init()
    {
        ClientBootstrap.init();
    }

    public static void registerReloadListener(RegisterClientReloadListenersEvent event)
    {
        event.registerReloadListener(JsonDataManager.getInstance());
    }
}
