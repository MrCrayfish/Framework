package com.mrcrayfish.framework.client;

import com.mrcrayfish.framework.api.event.ClientConnectionEvents;
import com.mrcrayfish.framework.api.event.TickEvents;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

/**
 * Author: MrCrayfish
 */
public class FabricClientEvents implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            ClientConnectionEvents.LOGGING_IN.post().handle(client.player, client.gameMode, handler.getConnection());
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            ClientConnectionEvents.LOGGING_OUT.post().handle();
        });
    }
}
