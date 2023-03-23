package com.mrcrayfish.framework.api.event;

import com.mrcrayfish.framework.event.IScreenEvent;

/**
 * Author: MrCrayfish
 */
public class ScreenEvents
{
    public static final FrameworkEvent<IScreenEvent.AfterDrawContainerBackground> AFTER_DRAW_CONTAINER_BACKGROUND = new FrameworkEvent<>(listeners -> (screen, stack, mouseX, mouseY) -> {
       listeners.forEach(listener -> listener.handle(screen, stack, mouseX, mouseY));
    });
}
