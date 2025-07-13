package com.mrcrayfish.framework.client;

import com.mrcrayfish.framework.api.event.InputEvents;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.common.MinecraftForge;

/**
 * Author: MrCrayfish
 */
public final class ClientFrameworkForge
{
    public static void init()
    {
        ClientBootstrap.init();
        MinecraftForge.EVENT_BUS.register(new ClientForgeEvents());
        InputEvents.REGISTER_KEY_MAPPING.post().handle(ClientRegistry::registerKeyBinding);
    }

    public static void registerReloadListener(RegisterClientReloadListenersEvent event)
    {
        event.registerReloadListener(JsonDataManager.getInstance());
    }
}
