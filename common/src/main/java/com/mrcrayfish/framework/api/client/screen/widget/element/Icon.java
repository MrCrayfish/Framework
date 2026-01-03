package com.mrcrayfish.framework.api.client.screen.widget.element;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

/**
 * Represents an abstract graphical icon that can be drawn at a specific position
 * and has a defined width and height dimension. Concrete implementations define
 * its appearance and behaviour.
 */
public abstract class Icon
{
    /**
     * @return The width of this icon in pixel units; may vary depending on implementation
     */
    public abstract int width();

    /**
     * @return The height of this icon in pixel units; may vary depending on implementation
     */
    public abstract int height();

    /**
     * Draws this icon at the given position
     *
     * @param graphics    a {@link GuiGraphics} instance
     * @param x           the x pos to draw the icon
     * @param y           the y pos to draw the icon
     * @param partialTick the current partial tick value
     */
    public abstract void draw(GuiGraphics graphics, int x, int y, float partialTick);

    /**
     * Creates an {@link Icon} instance that represents a sprite using the specified resource location, width, and height.
     *
     * @param resource the {@link ResourceLocation} of the sprite
     * @param width    the width of the sprite in pixels
     * @param height   the height of the sprite in pixels
     * @return an {@link Icon} instance representing the sprite
     */
    public static Icon sprite(ResourceLocation resource, int width, int height)
    {
        return new Sprite(resource, width, height);
    }

    /**
     * Creates an {@link Icon} instance that represents a sprite using the given resource supplier,
     * width, and height.
     *
     * @param resource a supplier providing the {@link ResourceLocation} for the sprite
     * @param width    the width of the sprite in pixels
     * @param height   the height of the sprite in pixels
     * @return an {@link Icon} instance representing the sprite
     */
    public static Icon sprite(Supplier<ResourceLocation> resource, int width, int height)
    {
        return new Sprite(resource, width, height);
    }

    private static final class Sprite extends Icon
    {
        private final Supplier<ResourceLocation> resource;
        private final int width;
        private final int height;

        private Sprite(Supplier<ResourceLocation> resource, int width, int height)
        {
            this.resource = resource;
            this.width = width;
            this.height = height;
        }

        private Sprite(ResourceLocation resource, int width, int height)
        {
            this(() -> resource, width, height);
        }

        @Override
        public int width()
        {
            return this.width;
        }

        @Override
        public int height()
        {
            return this.height;
        }

        @Override
        public void draw(GuiGraphics graphics, int x, int y, float partialTick)
        {
            graphics.blitSprite(this.resource.get(), x, y, this.width, this.height);
        }
    }
}
