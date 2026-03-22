package com.mrcrayfish.framework.api.client.screen.overlay;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * Manages a stack-based overlay system for an {@link Overlayable} screen.
 */
public final class OverlayController implements ContainerEventHandler
{
    private final Overlayable overlayable;
    private final Deque<Overlay> stack = new ArrayDeque<>();

    @Nullable GuiEventListener focused;
    boolean dragging;

    public OverlayController(Overlayable overlayable)
    {
        this.overlayable = overlayable;
    }

    /**
     * Attempts to open the specified {@link Overlay} on this controller.
     *
     * @param overlay the {@link Overlay} instance to open
     * @return {@code true} if the overlay was successfully opened; {@code false}
     * otherwise (e.g. when already in the stack or is owned by another controller)
     */
    boolean open(Overlay overlay)
    {
        Overlayable owner = overlay.owner;
        if(owner != null && owner != this.overlayable)
            return false;

        if(this.stack.contains(overlay))
            return false;

        this.setFocused(null);
        this.bind(overlay);
        this.stack.push(overlay);
        return true;
    }

    /**
     * Attempts to close the specified {@link Overlay} on this controller. If the provided overlay
     * is not managed by this controller (aka not in the stack), it will simply be ignored.
     *
     * @param overlay the {@link Overlay} instance to close
     */
    void close(Overlay overlay)
    {
        Overlayable owner = overlay.owner;
        if(owner == null || owner != this.overlayable)
            return;

        if(!this.stack.contains(overlay))
            return;
        
        while(!this.stack.isEmpty())
        {
            Overlay current = this.stack.pop();
            this.unbind(current);
            if(current.equals(overlay))
            {
                break;
            }
        }

        this.setFocused(null);
    }

    /**
     * Closes every overlay currently managed by this controller.
     */
    public void closeAll()
    {
        while(!this.stack.isEmpty())
        {
            Overlay overlay = this.stack.pop();
            this.unbind(overlay);
        }

        this.setFocused(null);
    }

    /**
     * Closes all overlays that were opened after the specified {@code target} overlay. If the
     * overlay is not managed by this controller, this method will do nothing.
     *
     * @param target the overlay whose upper layers should be closed
     */
    public void closeAbove(Overlay target)
    {
        // Prevent closing controller doesn't manage the target
        if(!this.stack.contains(target))
            return;

        // Peek until it doesn't find the target and close the overlay
        while(!target.equals(this.stack.peek()))
        {
            this.unbind(this.stack.pop());
            this.setFocused(null);
        }
    }

    /**
     * Binds the provided overlay to this controller. This will set the {@link Overlay#owner} in the
     * {@link Overlay} instance, and send the opened event.
     *
     * @param overlay the {@link Overlay} to bind
     */
    private void bind(Overlay overlay)
    {
        overlay.owner = this.overlayable;
        overlay.onOpened();
    }

    /**
     * Unbinds the provided overlay to this controller. This will set the {@link Overlay#owner} to
     * {@code null}, and send the closed event.
     *
     * @param overlay the {@link Overlay} to bind
     */
    private void unbind(Overlay overlay)
    {
        overlay.owner = null;
        overlay.onClosed();
    }

    /**
     * @return {@code true} if input should be blocked, otherwise {@code false}.
     */
    @ApiStatus.Internal
    public boolean blocksInput()
    {
        return !this.stack.isEmpty();
    }

    /**
     * Renders all overlays managed by this controller.
     *
     * @param extractor   the {@link GuiGraphicsExtractor} instance used for drawing operations
     * @param mouseX      the current x position of the mouse cursor
     * @param mouseY      the current y position of the mouse cursor
     * @param partialTick the interpolation factor between ticks, used for smooth animations
     */
    public void render(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick)
    {
        Matrix3x2fStack pose = extractor.pose();
        for(Overlay overlay : this.stack)
        {
            // Translate on z so overlays don't mess with each other on depth buffer
            // This is terrible but solved once updated to 1.21.11+
            extractor.nextStratum(); // TODO 1.12.11 TEST
            overlay.renderBackground(extractor);
            overlay.render(extractor, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public List<? extends GuiEventListener> children()
    {
        Overlay top = this.stack.peek();
        return top != null ? top.getWidgets() : List.of();
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick)
    {
        // Send the mouse-clicked event to the top-level overlay
        if(ContainerEventHandler.super.mouseClicked(event, doubleClick))
            return true;

        // Otherwise while cascading down from the top overlay, if the click occurred within the
        // interactable area of an overlay, close all overlays above.
        for(Overlay overlay : this.stack)
        {
            if(overlay.getInteractableArea().containsPoint((int) event.x(), (int) event.y()))
            {
                this.setFocused(null);
                this.closeAbove(overlay);
                return true;
            }
        }

        // If nothing was clicked at all, close all overlays
        this.closeAll();

        return true;
    }

    @Override
    public boolean keyPressed(KeyEvent event)
    {
        // Allows the user to press escape to close the top overlay
        if(event.key() == GLFW.GLFW_KEY_ESCAPE)
        {
            Overlay overlay = this.stack.peek();
            if(overlay != null)
            {
                this.close(overlay);
                return true;
            }
        }
        return ContainerEventHandler.super.keyPressed(event);
    }

    @Override
    @Nullable
    public GuiEventListener getFocused()
    {
        return this.focused;
    }

    @Override
    public void setFocused(@Nullable GuiEventListener listener)
    {
        if(this.focused != null)
        {
            this.focused.setFocused(false);
        }
        if(listener != null)
        {
            listener.setFocused(true);
        }
        this.focused = listener;
    }

    @Override
    public boolean isDragging()
    {
        return this.dragging;
    }

    @Override
    public void setDragging(boolean dragging)
    {
        this.dragging = dragging;
    }
}
