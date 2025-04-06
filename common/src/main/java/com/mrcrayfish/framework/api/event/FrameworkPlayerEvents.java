package com.mrcrayfish.framework.api.event;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.Container;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Author: MrCrayfish
 */
public final class FrameworkPlayerEvents
{
    public static final FrameworkEvent<StartedTrackingEntity> STARTED_TRACKING_ENTITY = new FrameworkEvent<>(listeners -> (entity, player) -> {
        listeners.forEach(event -> event.handle(entity, player));
    });

    public static final FrameworkEvent<StoppedTrackingEntity> STOPPED_TRACKING_ENTITY = new FrameworkEvent<>(listeners -> (entity, player) -> {
        listeners.forEach(event -> event.handle(entity, player));
    });

    public static final FrameworkEvent<Copy> COPY = new FrameworkEvent<>(listeners -> (oldPlayer, newPlayer, conqueredEnd) -> {
        listeners.forEach(event -> event.handle(oldPlayer, newPlayer, conqueredEnd));
    });

    public static final FrameworkEvent<ChangeDimension> CHANGE_DIMENSION = new FrameworkEvent<>(listeners -> (player, oldDimension, newDimension) -> {
        listeners.forEach(event -> event.handle(player, oldDimension, newDimension));
    });

    public static final FrameworkEvent<Respawn> RESPAWN = new FrameworkEvent<>(listeners -> (player, finishedGame) -> {
        listeners.forEach(event -> event.handle(player, finishedGame));
    });

    public static final FrameworkEvent<LoggedIn> LOGGED_IN = new FrameworkEvent<>(listeners -> (player) -> {
        listeners.forEach(event -> event.handle(player));
    });

    public static final FrameworkEvent<LoggedOut> LOGGED_OUT = new FrameworkEvent<>(listeners -> (player) -> {
        listeners.forEach(event -> event.handle(player));
    });

    public static final FrameworkEvent<PickupItem> PICKUP_ITEM = new FrameworkEvent<>(listeners -> (player, itemEntity) -> {
        for(var listener : listeners) {
            if(listener.handle(player, itemEntity)) {
                return true;
            }
        }
        return false;
    });

    public static final FrameworkEvent<CraftedItem> CRAFTED_ITEM = new FrameworkEvent<>(listeners -> (player, stack, container) -> {
        listeners.forEach(event -> event.handle(player, stack, container));
    });

    public static final FrameworkEvent<PickupExperience> PICKUP_EXPERIENCE = new FrameworkEvent<>(listeners -> (player, experienceOrb) -> {
        for(var listener : listeners) {
            if(listener.handle(player, experienceOrb)) {
                return true;
            }
        }
        return false;
    });

    public static final FrameworkEvent<Death> DEATH = new FrameworkEvent<>(listeners -> (player, damageSource) -> {
        for(var listener : listeners) {
            if(listener.handle(player, damageSource)) {
                return true;
            }
        }
        return false;
    });

    @FunctionalInterface
    public interface StartedTrackingEntity
    {
        void handle(Entity entity, Player player);
    }

    @FunctionalInterface
    public interface StoppedTrackingEntity
    {
        void handle(Entity entity, Player player);
    }

    @FunctionalInterface
    public interface Copy
    {
        void handle(Player oldPlayer, Player newPlayer, boolean conqueredEnd);
    }

    @FunctionalInterface
    public interface ChangeDimension
    {
        void handle(Player player, ResourceKey<Level> oldDimension, ResourceKey<Level> newDimension);
    }

    @FunctionalInterface
    public interface Respawn
    {
        void handle(Player player, boolean conqueredEnd);
    }

    @FunctionalInterface
    public interface LoggedIn
    {
        void handle(Player player);
    }

    @FunctionalInterface
    public interface LoggedOut
    {
        void handle(Player player);
    }

    @FunctionalInterface
    public interface PickupItem
    {
        boolean handle(Player player, ItemEntity entity);
    }

    @FunctionalInterface
    public interface CraftedItem
    {
        void handle(Player player, ItemStack stack, Container inventory);
    }

    @FunctionalInterface
    public interface PickupExperience
    {
        boolean handle(Player player, ExperienceOrb orb);
    }

    @FunctionalInterface
    public interface Death
    {
        boolean handle(Player player, DamageSource source);
    }
}
