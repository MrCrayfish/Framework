package com.mrcrayfish.framework.api.client.screen;

import com.mrcrayfish.framework.api.client.screen.overlay.OverlayController;
import com.mrcrayfish.framework.api.client.screen.overlay.Overlayable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * A custom base class for screens and includes support for Framework's overlay system.
 */
public abstract class FrameworkScreen extends Screen implements Overlayable
{
    private final OverlayController controller = new OverlayController(this);

    protected FrameworkScreen(Component title)
    {
        super(title);
    }

    @Override
    public final OverlayController getOverlayController()
    {
        return this.controller;
    }
}
