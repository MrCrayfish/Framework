package com.mrcrayfish.framework.client;

import com.mrcrayfish.framework.api.client.event.FrameworkClientEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.fml.ModLoader;

/**
 * Author: MrCrayfish
 */
public final class ClientHandler
{
    public static void registerReloadListener(RegisterClientReloadListenersEvent event)
    {
        event.registerReloadListener(JsonDataManager.getInstance());
        ModLoader.get().postEvent(new FrameworkClientEvent.Register());
    }
}
