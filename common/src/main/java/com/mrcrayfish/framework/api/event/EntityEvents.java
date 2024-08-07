package com.mrcrayfish.framework.api.event;

import com.mrcrayfish.framework.event.IEntityEvent;

/**
 * Author: MrCrayfish
 */
public final class EntityEvents
{
    public static final FrameworkEvent<IEntityEvent.JoinLevel> JOIN_LEVEL = new FrameworkEvent<>(listeners -> (entity, level, disk) -> {
        listeners.forEach(start -> start.handle(entity, level, disk));
    });

    public static final FrameworkEvent<IEntityEvent.LeaveLevel> LEAVE_LEVEL = new FrameworkEvent<>(listeners -> (entity, level) -> {
        listeners.forEach(start -> start.handle(entity, level));
    });

    public static final FrameworkEvent<IEntityEvent.LivingEntityDeath> LIVING_ENTITY_DEATH = new FrameworkEvent<>(listeners -> (entity, damageSource) -> {
        for(var listener : listeners) {
            if(listener.handle(entity, damageSource)) {
                return true;
            }
        }
        return false;
    });
}
