package com.mrcrayfish.framework.api.event;

import com.mrcrayfish.framework.event.IClientEvent;

/**
 * Author: MrCrayfish
 */
public class ClientEvents
{
    public static final FrameworkEvent<IClientEvent.PlayerInputUpdate> PLAYER_INPUT_UPDATE = new FrameworkEvent<>(listeners -> (player, input) -> {
        listeners.forEach(listener -> listener.handle(player, input));
    });
}
