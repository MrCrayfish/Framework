package com.mrcrayfish.framework.api.config.event;

import com.mrcrayfish.framework.api.event.FrameworkEvent;

/**
 * Author: MrCrayfish
 */
public class FrameworkConfigEvents
{
    public static final FrameworkEvent<Load> LOAD = new FrameworkEvent<>(listeners -> object -> {
       listeners.forEach(listener -> listener.handle(object));
    });

    public static final FrameworkEvent<Unload> UNLOAD = new FrameworkEvent<>(listeners -> object -> {
        listeners.forEach(listener -> listener.handle(object));
    });

    public static final FrameworkEvent<Reload> RELOAD = new FrameworkEvent<>(listeners -> object -> {
        listeners.forEach(listener -> listener.handle(object));
    });

    public interface Load
    {
        void handle(Object object);
    }

    public interface Unload
    {
        void handle(Object object);
    }

    public interface Reload
    {
        void handle(Object object);
    }
}
