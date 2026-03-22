package com.mrcrayfish.framework.api.client.screen;

import com.mrcrayfish.framework.api.client.screen.overlay.OverlayController;
import com.mrcrayfish.framework.api.client.screen.overlay.Overlayable;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.NotNull;

/**
 * A custom base class for container screens and includes support for Framework's overlay system.
 */
public abstract class FrameworkContainerScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<@NotNull T> implements Overlayable
{
    private final OverlayController controller = new OverlayController(this);

    protected FrameworkContainerScreen(T menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title);
    }

    @Override
    public final OverlayController getOverlayController()
    {
        return this.controller;
    }
}
