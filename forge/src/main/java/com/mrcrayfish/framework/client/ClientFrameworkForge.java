package com.mrcrayfish.framework.client;

import com.mrcrayfish.framework.api.event.InputEvents;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
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
    }

    public static void registerKeyMappings(RegisterKeyMappingsEvent event)
    {
        InputEvents.REGISTER_KEY_MAPPING.post().handle(event::register);
    }

    public static void registerReloadListener(RegisterClientReloadListenersEvent event)
    {
        event.registerReloadListener(JsonDataManager.getInstance());
    }
}
