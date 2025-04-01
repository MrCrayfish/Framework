package com.mrcrayfish.framework.event;

import com.mrcrayfish.framework.api.event.IFrameworkEvent;
import net.minecraft.client.player.ClientInput;
import net.minecraft.world.entity.player.Player;

/**
 * Author: MrCrayfish
 */
public interface IClientEvent extends IFrameworkEvent
{
    @FunctionalInterface
    interface PlayerInputUpdate extends IClientEvent
    {
        void handle(Player player, ClientInput input);
    }

    @FunctionalInterface
    interface ClientRegistryInitialization extends IClientEvent
    {
        void handle();
    }
}
