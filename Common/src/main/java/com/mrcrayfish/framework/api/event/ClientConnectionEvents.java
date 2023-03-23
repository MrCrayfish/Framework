package com.mrcrayfish.framework.api.event;

import com.mrcrayfish.framework.event.IClientConnectionEvent;

/**
 * Author: MrCrayfish
 */
public class ClientConnectionEvents
{
    public static final FrameworkEvent<IClientConnectionEvent.LoggingIn> LOGGING_IN = new FrameworkEvent<>(listeners -> (player, gameMode, connection) -> {
        listeners.forEach(listener -> listener.handle(player, gameMode, connection));
    });

    public static final FrameworkEvent<IClientConnectionEvent.LoggingOut> LOGGING_OUT = new FrameworkEvent<>(listeners -> () -> {
        listeners.forEach(IClientConnectionEvent.LoggingOut::handle);
    });
}
