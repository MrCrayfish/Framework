package com.mrcrayfish.framework.api.event.client;

import com.mrcrayfish.framework.api.event.FrameworkEvent;
import net.minecraft.client.DeltaTracker;

public final class FrameworkClientTickEvents
{
    public static final FrameworkEvent<StartClient> START_CLIENT = new FrameworkEvent<>(listeners -> () -> {
        listeners.forEach(StartClient::handle);
    });

    public static final FrameworkEvent<EndClient> END_CLIENT = new FrameworkEvent<>(listeners -> () -> {
        listeners.forEach(EndClient::handle);
    });

    public static final FrameworkEvent<StartRender> START_RENDER = new FrameworkEvent<>(listeners -> tracker -> {
        listeners.forEach(listener -> listener.handle(tracker));
    });

    public static final FrameworkEvent<EndRender> END_RENDER = new FrameworkEvent<>(listeners -> tracker -> {
        listeners.forEach(listener -> listener.handle(tracker));
    });

    @FunctionalInterface
    public interface StartClient
    {
        void handle();
    }

    @FunctionalInterface
    public interface EndClient
    {
        void handle();
    }

    @FunctionalInterface
    public interface StartRender
    {
        void handle(DeltaTracker tracker);
    }

    @FunctionalInterface
    public interface EndRender
    {
        void handle(DeltaTracker tracker);
    }
}
