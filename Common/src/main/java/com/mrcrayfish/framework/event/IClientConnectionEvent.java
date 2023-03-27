package com.mrcrayfish.framework.event;

import com.mrcrayfish.framework.api.event.IFrameworkEvent;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.Connection;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public interface IClientConnectionEvent extends IFrameworkEvent
{
    @FunctionalInterface
    interface LoggingIn extends IClientConnectionEvent
    {
        void handle(LocalPlayer player, MultiPlayerGameMode gameMode, Connection connection);
    }

    @FunctionalInterface
    interface LoggingOut extends IClientConnectionEvent
    {
        void handle(@Nullable Connection connection);
    }
}
