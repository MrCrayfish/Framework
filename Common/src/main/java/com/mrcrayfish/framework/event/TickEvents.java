package com.mrcrayfish.framework.event;

import com.mrcrayfish.framework.event.api.ITickEvent;

/**
 * Author: MrCrayfish
 */
public final class TickEvents
{
    public static final FrameworkEvent<ITickEvent.StartServer> START_SERVER = new FrameworkEvent<>(listeners -> (server) -> {
        listeners.forEach(listener -> listener.handle(server));
    });

    public static final FrameworkEvent<ITickEvent.EndServer> END_SERVER = new FrameworkEvent<>(listeners -> (server) -> {
        listeners.forEach(listener -> listener.handle(server));
    });
}
