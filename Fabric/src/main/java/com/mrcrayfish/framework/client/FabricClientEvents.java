package com.mrcrayfish.framework.client;

import com.mrcrayfish.framework.api.event.ClientConnectionEvents;
import com.mrcrayfish.framework.api.event.ScreenEvents;
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
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            TickEvents.START_CLIENT.post().handle();
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            TickEvents.END_CLIENT.post().handle();
        });
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            ClientConnectionEvents.LOGGING_IN.post().handle(client.player, client.gameMode, handler.getConnection());
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            ClientConnectionEvents.LOGGING_OUT.post().handle(handler.getConnection());
        });
        net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            ScreenEvents.INIT.post().handle(screen);
        });
        net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.beforeRender(screen).register((screen1, poseStack, mouseX, mouseY, partialTick) -> {
                ScreenEvents.BEFORE_DRAW.post().handle(screen, poseStack, mouseX, mouseY, partialTick);
            });
            net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.afterRender(screen).register((screen1, poseStack, mouseX, mouseY, partialTick) -> {
                ScreenEvents.AFTER_DRAW.post().handle(screen, poseStack, mouseX, mouseY, partialTick);
            });
        });
    }
}
