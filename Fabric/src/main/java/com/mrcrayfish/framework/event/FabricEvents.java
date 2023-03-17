package com.mrcrayfish.framework.event;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;

/**
 * Author: MrCrayfish
 */
public class FabricEvents implements ModInitializer
{
    @Override
    public void onInitialize()
    {
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            TickEvents.START_SERVER.post().handle(server);
        });
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            TickEvents.START_SERVER.post().handle(server);
        });
        EntityTrackingEvents.START_TRACKING.register((entity, player) -> {
            PlayerEvents.START_TRACKING_ENTITY.post().handle(entity, player);
        });
        EntityTrackingEvents.STOP_TRACKING.register((entity, player) -> {
            PlayerEvents.END_TRACKING_ENTITY.post().handle(entity, player);
        });
        ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> {
            EntityEvents.JOIN_LEVEL.post().handle(entity, level, false);
        });
        ServerEntityEvents.ENTITY_UNLOAD.register((entity, level) -> {
            EntityEvents.LEAVE_LEVEL.post().handle(entity, level);
        });
        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
            PlayerEvents.COPY.post().handle(oldPlayer, newPlayer, !alive);
        });
    }
}
