package com.mrcrayfish.framework.event;

import com.mrcrayfish.framework.api.event.IFrameworkEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
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

    interface LivingEntityDeath extends IEntityEvent
    {
        boolean handle(LivingEntity entity, DamageSource source);
    }
}
