package com.mrcrayfish.framework.api.client.screen.widget.texture;

import com.mrcrayfish.framework.api.client.screen.widget.layout.Border;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

/**
 * Represents a nine-slice texture. Please note that the source texture must be 256x256
 */
public final class NineSliceTexture extends FrameworkTexture
{
    private final ResourceLocation source;
    private final int textureU, textureV;
    private final int textureWidth, textureHeight;
    private final Border border;

    NineSliceTexture(ResourceLocation source, int textureU, int textureV, int textureWidth, int textureHeight, Border border)
    {
        this.source = source;
        this.border = border;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.textureU = textureU;
        this.textureV = textureV;
    }

    @Override
    public int width()
    {
        return this.textureWidth;
    }

    @Override
    public int height()
    {
        return this.textureHeight;
    }

    @Override
    public void draw(GuiGraphics graphics, int x, int y, int width, int height)
    {
        graphics.blitNineSliced(this.source, x, y, width, height, this.border.left(), this.border.top(), this.border.right(), this.border.bottom(), this.textureWidth, this.textureHeight, this.textureU, this.textureV);
    }
}
