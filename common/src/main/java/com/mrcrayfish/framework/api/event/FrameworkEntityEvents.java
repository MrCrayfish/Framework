package com.mrcrayfish.framework.api.event;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

/**
 * Author: MrCrayfish
 */
public final class FrameworkEntityEvents
{
    public static final FrameworkEvent<JoinLevel> JOIN_LEVEL = new FrameworkEvent<>(listeners -> (entity, level, disk) -> {
        listeners.forEach(start -> start.handle(entity, level, disk));
    });

    public static final FrameworkEvent<LeaveLevel> LEAVE_LEVEL = new FrameworkEvent<>(listeners -> (entity, level) -> {
        listeners.forEach(start -> start.handle(entity, level));
    });

    public static final FrameworkEvent<LivingEntityDeath> LIVING_ENTITY_DEATH = new FrameworkEvent<>(listeners -> (entity, damageSource) -> {
        for(var listener : listeners) {
            if(listener.handle(entity, damageSource)) {
                return true;
            }
        }
        return false;
    });

    @FunctionalInterface
    public interface JoinLevel
    {
        void handle(Entity entity, Level level, boolean disk);
    }

    @FunctionalInterface
    public interface LeaveLevel
    {
        void handle(Entity entity, Level level);
    }

    @FunctionalInterface
    public interface LivingEntityDeath
    {
        boolean handle(LivingEntity entity, DamageSource source);
    }
}
