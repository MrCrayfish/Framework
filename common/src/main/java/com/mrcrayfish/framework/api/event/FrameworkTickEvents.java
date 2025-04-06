package com.mrcrayfish.framework.api.event;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Author: MrCrayfish
 */
public final class FrameworkTickEvents
{
    public static final FrameworkEvent<StartServer> START_SERVER = new FrameworkEvent<>(listeners -> (server) -> {
        listeners.forEach(listener -> listener.handle(server));
    });

    public static final FrameworkEvent<EndServer> END_SERVER = new FrameworkEvent<>(listeners -> (server) -> {
        listeners.forEach(listener -> listener.handle(server));
    });

    public static final FrameworkEvent<StartLevel> START_LEVEL = new FrameworkEvent<>(listeners -> (server) -> {
        listeners.forEach(listener -> listener.handle(server));
    });

    public static final FrameworkEvent<EndLevel> END_LEVEL = new FrameworkEvent<>(listeners -> (server) -> {
        listeners.forEach(listener -> listener.handle(server));
    });

    public static final FrameworkEvent<StartPlayer> START_PLAYER = new FrameworkEvent<>(listeners -> (player) -> {
        listeners.forEach(listener -> listener.handle(player));
    });

    public static final FrameworkEvent<EndPlayer> END_PLAYER = new FrameworkEvent<>(listeners -> (player) -> {
        listeners.forEach(listener -> listener.handle(player));
    });

    public static final FrameworkEvent<StartLivingEntity> START_LIVING_ENTITY = new FrameworkEvent<>(listeners -> (entity) -> {
        listeners.forEach(listener -> listener.handle(entity));
    });

    public static final FrameworkEvent<EndLivingEntity> END_LIVING_ENTITY = new FrameworkEvent<>(listeners -> (entity) -> {
        listeners.forEach(listener -> listener.handle(entity));
    });

    @FunctionalInterface
    public interface StartServer
    {
        void handle(MinecraftServer server);
    }

    @FunctionalInterface
    public interface EndServer
    {
        void handle(MinecraftServer server);
    }

    @FunctionalInterface
    public interface StartLevel
    {
        void handle(Level level);
    }

    @FunctionalInterface
    public interface EndLevel
    {
        void handle(Level level);
    }

    @FunctionalInterface
    public interface StartPlayer
    {
        void handle(Player player);
    }

    @FunctionalInterface
    public interface EndPlayer
    {
        void handle(Player player);
    }

    @FunctionalInterface
    public interface StartLivingEntity
    {
        void handle(LivingEntity entity);
    }

    @FunctionalInterface
    public interface EndLivingEntity
    {
        void handle(LivingEntity entity);
    }
}
