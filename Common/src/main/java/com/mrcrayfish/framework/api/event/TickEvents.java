package com.mrcrayfish.framework.api.event;

import com.mrcrayfish.framework.event.ITickEvent;

/**
 * Author: MrCrayfish
 */
public final class TickEvents
{
    public static final FrameworkEvent<ITickEvent.StartClient> START_CLIENT = new FrameworkEvent<>(listeners -> () -> {
        listeners.forEach(ITickEvent.StartClient::handle);
    });

    public static final FrameworkEvent<ITickEvent.EndClient> END_CLIENT = new FrameworkEvent<>(listeners -> () -> {
        listeners.forEach(ITickEvent.EndClient::handle);
    });

    public static final FrameworkEvent<ITickEvent.StartServer> START_SERVER = new FrameworkEvent<>(listeners -> (server) -> {
        listeners.forEach(listener -> listener.handle(server));
    });

    public static final FrameworkEvent<ITickEvent.EndServer> END_SERVER = new FrameworkEvent<>(listeners -> (server) -> {
        listeners.forEach(listener -> listener.handle(server));
    });

    public static final FrameworkEvent<ITickEvent.StartPlayer> START_PLAYER = new FrameworkEvent<>(listeners -> (player) -> {
        listeners.forEach(listener -> listener.handle(player));
    });

    public static final FrameworkEvent<ITickEvent.EndPlayer> END_PLAYER = new FrameworkEvent<>(listeners -> (player) -> {
        listeners.forEach(listener -> listener.handle(player));
    });

    public static final FrameworkEvent<ITickEvent.StartLivingEntity> START_LIVING_ENTITY = new FrameworkEvent<>(listeners -> (entity) -> {
        listeners.forEach(listener -> listener.handle(entity));
    });

    public static final FrameworkEvent<ITickEvent.StartRender> START_RENDER = new FrameworkEvent<>(listeners -> (partialTick) -> {
        listeners.forEach(listener -> listener.handle(partialTick));
    });

    public static final FrameworkEvent<ITickEvent.EndRender> END_RENDER = new FrameworkEvent<>(listeners -> (partialTick) -> {
        listeners.forEach(listener -> listener.handle(partialTick));
    });
}
