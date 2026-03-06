package com.mrcrayfish.framework.api.client.screen.overlay;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

/**
 * Represents a visual overlay that can be displayed on an {@link Overlayable} screen.
 */
public abstract class Overlay
{
    @Nullable Overlayable owner;

    /**
     * Retrieves the area of the screen which this overlay can receive mouse interaction events. The
     * returned {@link ScreenRectangle} defines the bounds which the cursor position must be within
     * for this overlay to receive mouse events, like mouse button down. Events that don't occur within the bounds are considered an
     * outside click, and events won't be sent to this overlay.
     *
     * @return a {@code ScreenRectangle} representing the interactable region of this overlay; may be
     *         {@code null} if the overlay does not expose an interactable area.
     */
    public abstract ScreenRectangle getInteractableArea();

    /**
     * Returns the widgets that belong to this overlay.
     *
     * @return a {@link List} containing all {@link GuiEventListener} instances in this overlay.
     */
    public abstract List<? extends GuiEventListener> getWidgets();

    /**
     * The main overlay render call. Implementation specific.
     *
     * @param graphics    a {@link GuiGraphics} instance
     * @param mouseX      the current x position of the mouse
     * @param mouseY      the current y position of the mouse
     * @param partialTick the current partial tick
     */
    public abstract void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick);

    /**
     * Renders the background for this overlay. The default implementation will fill the entire
     * viewport with a semi-transparent black colour.
     *
     * @param graphics a {@link GuiGraphics} instance used for rendering
     */
    public void renderBackground(GuiGraphics graphics)
    {
        Window window = Minecraft.getInstance().getWindow();
        graphics.fill(0, 0, window.getWidth(), window.getHeight(), 0x50000000);
    }

    /**
     * Invoked after the overlay has been successfully opened.
     */
    protected void onOpened() {}

    /**
     * Invoked after the overlay has been successfully closed.
     */
    protected void onClosed() {}

    /**
     * Attempts to open this overlay on the current screen, and on success, the provided consumer
     * will be called with a rectangle representing the viewport bounds. For an overlay to open
     * successfully, the overlay must not be open yet and the current screen must support overlays
     * as per an {@link Overlayable}. The provided consumer can be used to update the positions of
     * any elements in the overlay.
     *
     * @param positioner a consumer that accepts a {@link ScreenRectangle}; used for post positioning
     *                   of elements after opening
     */
    public final void open(Consumer<ScreenRectangle> positioner)
    {
        Screen screen = Minecraft.getInstance().screen;
        if(screen instanceof Overlayable overlayable)
        {
            if(overlayable.getOverlayController().open(this))
            {
                positioner.accept(screen.getRectangle());
            }
        }
    }

    /**
     * Closes this overlay. If the overlay is already closed, this will do nothing.
     */
    public final void close()
    {
        if(this.owner != null)
        {
            this.owner.getOverlayController().close(this);
        }
    }

    /**
     * Closes this overlay and every other overlay currently open. This is useful if an action is
     * performed deep into multiple overlays, it can close all of them.
     */
    public final void deepClose()
    {
        if(this.owner != null)
        {
            this.owner.getOverlayController().closeAll();
        }
    }

    /**
     * Indicates whether the overlay is currently open.
     */
    public final boolean isOpened()
    {
        return this.owner != null && this.owner.isDisplayed();
    }

    /**
     * Retrieves the {@link Overlayable} this overlay is currently attached to.
     *
     * @return the attached {@link Overlayable} or {@code null} if not attached to anything.
     */
    @Nullable
    public Overlayable getOwner()
    {
        return this.owner;
    }
}
