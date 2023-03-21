package com.mrcrayfish.framework.event;

import com.mrcrayfish.framework.api.event.IFrameworkEvent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.Container;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Author: MrCrayfish
 */
public interface IPlayerEvent extends IFrameworkEvent
{
    interface StartTrackingEntity extends IPlayerEvent
    {
        void handle(Entity entity, Player player);
    }

    interface EndTrackingEntity extends IPlayerEvent
    {
        void handle(Entity entity, Player player);
    }

    interface Copy extends IPlayerEvent
    {
        void handle(Player oldPlayer, Player newPlayer, boolean conqueredEnd);
    }

    interface ChangeDimension extends IPlayerEvent
    {
        void handle(Player player, ResourceKey<Level> oldDimension, ResourceKey<Level> newDimension);
    }

    interface Respawn extends IPlayerEvent
    {
        void handle(Player player, boolean conqueredEnd);
    }

    interface LoggedIn extends IPlayerEvent
    {
        void handle(Player player);
    }

    interface LoggedOut extends IPlayerEvent
    {
        void handle(Player player);
    }

    interface PickupItem extends IPlayerEvent
    {
        boolean handle(Player player, ItemEntity entity);
    }

    interface CraftItem extends IPlayerEvent
    {
        void handle(Player player, ItemStack stack, Container inventory);
    }

    interface PickupExperience extends IPlayerEvent
    {
        boolean handle(Player player, ExperienceOrb orb);
    }

    interface Death extends IEntityEvent
    {
        boolean handle(Player player, DamageSource source);
    }
}
