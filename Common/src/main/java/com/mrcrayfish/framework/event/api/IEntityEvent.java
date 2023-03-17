package com.mrcrayfish.framework.event.api;

import com.mrcrayfish.framework.api.event.IFrameworkEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Author: MrCrayfish
 */
public interface IEntityEvent extends IFrameworkEvent
{
    interface JoinLevel extends IEntityEvent
    {
        void handle(Entity entity, Level level, boolean disk);
    }

    interface LeaveLevel extends IEntityEvent
    {
        void handle(Entity entity, Level level);
    }
}
