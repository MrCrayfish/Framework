package com.mrcrayfish.framework.event;

import com.mrcrayfish.framework.event.api.IPlayerEvent;

/**
 * Author: MrCrayfish
 */
public final class PlayerEvents
{
    public static final FrameworkEvent<IPlayerEvent.StartTrackingEntity> START_TRACKING_ENTITY = new FrameworkEvent<>(listeners -> (entity, player) -> {
        listeners.forEach(start -> start.handle(entity, player));
    });

    public static final FrameworkEvent<IPlayerEvent.EndTrackingEntity> END_TRACKING_ENTITY = new FrameworkEvent<>(listeners -> (entity, player) -> {
        listeners.forEach(start -> start.handle(entity, player));
    });

    public static final FrameworkEvent<IPlayerEvent.Copy> COPY = new FrameworkEvent<>(listeners -> (oldPlayer, newPlayer, respawn) -> {
        listeners.forEach(start -> start.handle(oldPlayer, newPlayer, respawn));
    });
}
