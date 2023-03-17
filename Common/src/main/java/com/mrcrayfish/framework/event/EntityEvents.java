package com.mrcrayfish.framework.event;

import com.mrcrayfish.framework.event.api.IEntityEvent;

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
}
