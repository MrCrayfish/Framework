package com.mrcrayfish.framework.api.event;

import net.minecraft.server.MinecraftServer;

/**
 * Author: MrCrayfish
 */
public final class FrameworkServerEvents
{
    public static final FrameworkEvent<Starting> STARTING = new FrameworkEvent<>(listeners -> (server) -> {
       listeners.forEach(listener -> listener.handle(server));
    });

    public static final FrameworkEvent<Started> STARTED = new FrameworkEvent<>(listeners -> (server) -> {
        listeners.forEach(listener -> listener.handle(server));
    });

    public static final FrameworkEvent<Stopping> STOPPING = new FrameworkEvent<>(listeners -> (server) -> {
        listeners.forEach(listener -> listener.handle(server));
    });

    public static final FrameworkEvent<Stopped> STOPPED = new FrameworkEvent<>(listeners -> (server) -> {
        listeners.forEach(listener -> listener.handle(server));
    });

    @FunctionalInterface
    public interface Starting
    {
        void handle(MinecraftServer server);
    }

    @FunctionalInterface
    public interface Started
    {
        void handle(MinecraftServer server);
    }

    @FunctionalInterface
    public interface Stopping
    {
        void handle(MinecraftServer server);
    }

    @FunctionalInterface
    public interface Stopped
    {
        void handle(MinecraftServer server);
    }
}
