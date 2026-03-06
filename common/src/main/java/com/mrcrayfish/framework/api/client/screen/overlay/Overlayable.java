package com.mrcrayfish.framework.api.client.screen.overlay;

import net.minecraft.client.Minecraft;

/**
 * Represents a screen that supports overlays.
 */
public interface Overlayable
{
    /**
     * @return the {@link OverlayController} responsible for managing overlays
     */
    OverlayController getOverlayController();

    /**
     * Indicates whether this overlayable is currently visible on screen.
     *
     * @return {@code true} if the overlayable is being displayed; {@code false} otherwise
     */
    default boolean isDisplayed()
    {
        return Minecraft.getInstance().screen == this;
    }
}
