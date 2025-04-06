package com.mrcrayfish.framework.event;

import com.mrcrayfish.framework.api.event.FrameworkEntityEvents;
import com.mrcrayfish.framework.api.event.FrameworkPlayerEvents;
import com.mrcrayfish.framework.api.event.FrameworkServerEvents;
import com.mrcrayfish.framework.api.event.FrameworkTickEvents;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
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
            FrameworkTickEvents.START_SERVER.post().handle(server);
        });
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            FrameworkTickEvents.END_SERVER.post().handle(server);
        });
        ServerTickEvents.START_WORLD_TICK.register(level -> {
            FrameworkTickEvents.START_LEVEL.post().handle(level);
        });
        ServerTickEvents.END_WORLD_TICK.register(level -> {
            FrameworkTickEvents.END_LEVEL.post().handle(level);
        });
        EntityTrackingEvents.START_TRACKING.register((entity, player) -> {
            FrameworkPlayerEvents.STARTED_TRACKING_ENTITY.post().handle(entity, player);
        });
        EntityTrackingEvents.STOP_TRACKING.register((entity, player) -> {
            FrameworkPlayerEvents.STOPPED_TRACKING_ENTITY.post().handle(entity, player);
        });
        ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> {
            FrameworkEntityEvents.JOIN_LEVEL.post().handle(entity, level, false);
        });
        ServerEntityEvents.ENTITY_UNLOAD.register((entity, level) -> {
            FrameworkEntityEvents.LEAVE_LEVEL.post().handle(entity, level);
        });
        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
            FrameworkPlayerEvents.COPY.post().handle(oldPlayer, newPlayer, !alive);
        });
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            FrameworkPlayerEvents.RESPAWN.post().handle(newPlayer, alive);
        });
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> {
            FrameworkPlayerEvents.CHANGE_DIMENSION.post().handle(player, origin.dimension(), destination.dimension());
        });
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            FrameworkServerEvents.STARTING.post().handle(server);
        });
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            FrameworkServerEvents.STARTED.post().handle(server);
        });
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            FrameworkServerEvents.STOPPING.post().handle(server);
        });
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            FrameworkServerEvents.STOPPED.post().handle(server);
        });
    }
}
