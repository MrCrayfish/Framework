package com.mrcrayfish.framework.event;

import com.mrcrayfish.framework.api.event.IFrameworkEvent;
import net.minecraft.client.player.Input;
import net.minecraft.world.entity.player.Player;

/**
 * Author: MrCrayfish
 */
public interface IClientEvent extends IFrameworkEvent
{
    @FunctionalInterface
    interface PlayerInputUpdate extends IClientEvent
    {
        void handle(Player player, Input input);
    }
}
