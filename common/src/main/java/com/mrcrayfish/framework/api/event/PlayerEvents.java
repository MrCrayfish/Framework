package com.mrcrayfish.framework.api.event;

import com.mrcrayfish.framework.event.IPlayerEvent;

/**
 * Author: MrCrayfish
 */
public final class PlayerEvents
{
    public static final FrameworkEvent<IPlayerEvent.StartTrackingEntity> START_TRACKING_ENTITY = new FrameworkEvent<>(listeners -> (entity, player) -> {
        listeners.forEach(event -> event.handle(entity, player));
    });

    public static final FrameworkEvent<IPlayerEvent.EndTrackingEntity> END_TRACKING_ENTITY = new FrameworkEvent<>(listeners -> (entity, player) -> {
        listeners.forEach(event -> event.handle(entity, player));
    });

    public static final FrameworkEvent<IPlayerEvent.Copy> COPY = new FrameworkEvent<>(listeners -> (oldPlayer, newPlayer, conqueredEnd) -> {
        listeners.forEach(event -> event.handle(oldPlayer, newPlayer, conqueredEnd));
    });

    public static final FrameworkEvent<IPlayerEvent.ChangeDimension> CHANGE_DIMENSION = new FrameworkEvent<>(listeners -> (player, oldDimension, newDimension) -> {
        listeners.forEach(event -> event.handle(player, oldDimension, newDimension));
    });

    public static final FrameworkEvent<IPlayerEvent.Respawn> RESPAWN = new FrameworkEvent<>(listeners -> (player, finishedGame) -> {
        listeners.forEach(event -> event.handle(player, finishedGame));
    });

    public static final FrameworkEvent<IPlayerEvent.LoggedIn> LOGGED_IN = new FrameworkEvent<>(listeners -> (player) -> {
        listeners.forEach(event -> event.handle(player));
    });

    public static final FrameworkEvent<IPlayerEvent.LoggedOut> LOGGED_OUT = new FrameworkEvent<>(listeners -> (player) -> {
        listeners.forEach(event -> event.handle(player));
    });

    public static final FrameworkEvent<IPlayerEvent.PickupItem> PICKUP_ITEM = new FrameworkEvent<>(listeners -> (player, itemEntity) -> {
        for(var listener : listeners) {
            if(listener.handle(player, itemEntity)) {
                return true;
            }
        }
        return false;
    });

    public static final FrameworkEvent<IPlayerEvent.CraftItem> CRAFT_ITEM = new FrameworkEvent<>(listeners -> (player, stack, container) -> {
        listeners.forEach(event -> event.handle(player, stack, container));
    });

    public static final FrameworkEvent<IPlayerEvent.PickupExperience> PICKUP_EXPERIENCE = new FrameworkEvent<>(listeners -> (player, experienceOrb) -> {
        for(var listener : listeners) {
            if(listener.handle(player, experienceOrb)) {
                return true;
            }
        }
        return false;
    });

    public static final FrameworkEvent<IPlayerEvent.Death> DEATH = new FrameworkEvent<>(listeners -> (player, damageSource) -> {
        for(var listener : listeners) {
            if(listener.handle(player, damageSource)) {
                return true;
            }
        }
        return false;
    });
}
