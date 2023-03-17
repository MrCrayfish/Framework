package com.mrcrayfish.framework.event.api;

import com.mrcrayfish.framework.api.event.IFrameworkEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

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
        void handle(Player oldPlayer, Player newPlayer, boolean respawn);
    }
}
