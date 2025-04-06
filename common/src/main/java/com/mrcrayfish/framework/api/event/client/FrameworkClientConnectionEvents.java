package com.mrcrayfish.framework.api.event.client;

import com.mrcrayfish.framework.api.event.FrameworkEvent;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.Connection;
import org.jetbrains.annotations.Nullable;

/**
 * Author: MrCrayfish
 */
public final class FrameworkClientConnectionEvents
{
    public static final FrameworkEvent<LoggingIn> LOGGING_IN = new FrameworkEvent<>(listeners -> (player, gameMode, connection) -> {
        listeners.forEach(listener -> listener.handle(player, gameMode, connection));
    });

    public static final FrameworkEvent<LoggingOut> LOGGING_OUT = new FrameworkEvent<>(listeners -> (connection) -> {
        listeners.forEach(listener -> listener.handle(connection));
    });

    @FunctionalInterface
    public interface LoggingIn
    {
        void handle(LocalPlayer player, MultiPlayerGameMode gameMode, Connection connection);
    }

    @FunctionalInterface
    public interface LoggingOut
    {
        void handle(@Nullable Connection connection);
    }
}
