package com.mrcrayfish.framework.api.event;

import com.mrcrayfish.framework.event.IServerEvent;

/**
 * Author: MrCrayfish
 */
public class ServerEvents
{
    public static final FrameworkEvent<IServerEvent.Starting> STARTING = new FrameworkEvent<>(listeners -> (server) -> {
       listeners.forEach(listener -> listener.handle(server));
    });

    public static final FrameworkEvent<IServerEvent.Started> STARTED = new FrameworkEvent<>(listeners -> (server) -> {
        listeners.forEach(listener -> listener.handle(server));
    });

    public static final FrameworkEvent<IServerEvent.Stopping> STOPPING = new FrameworkEvent<>(listeners -> (server) -> {
        listeners.forEach(listener -> listener.handle(server));
    });

    public static final FrameworkEvent<IServerEvent.Stopped> STOPPED = new FrameworkEvent<>(listeners -> (server) -> {
        listeners.forEach(listener -> listener.handle(server));
    });
}
