package com.mrcrayfish.framework.api.client.screen.widget.element;

import com.mrcrayfish.framework.api.client.screen.widget.texture.FrameworkTexture;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

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
     * Creates an {@link Icon} instance that represents an icon using the specified resource location, u, v, width, and height.
     * Please note that the source image must be 256 by 256 px.
     *
     * @param source the {@link ResourceLocation} of the icon
     * @param u      the u position of the icon on the image
     * @param v      the v position of the icon on the image
     * @param width  the width of the icon in pixels
     * @param height the height of the icon in pixels
     * @return an {@link Icon} instance representing the sprite
     */
    public static Icon create(ResourceLocation source, int u, int v, int width, int height)
    {
        return FrameworkTexture.subImage(source, u, v, width, height);
    }

    /**
     * Creates an {@link Icon} instance that represents an icon using the specified resource location, u, v, width, and height.
     *
     * @param source       the {@link ResourceLocation} of the icon
     * @param u            the u position of the icon on the image
     * @param v            the v position of the icon on the image
     * @param width        the width of the icon in pixels
     * @param height       the height of the icon in pixels
     * @param sourceWidth  the width of the source image
     * @param sourceHeight the height of the source image
     * @return an {@link Icon} instance representing the sprite
     */
    public static Icon create(ResourceLocation source, int u, int v, int width, int height, int sourceWidth, int sourceHeight)
    {
        return FrameworkTexture.subImage(source, u, v, width, height, sourceWidth, sourceHeight);
    }
}
