package com.mrcrayfish.framework.api.client.screen.widget.texture;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public final class NormalTexture extends FrameworkTexture
{
    private final ResourceLocation source;
    private final int sourceWidth, sourceHeight;
    private final int textureU, textureV;
    private final int textureWidth, textureHeight;

    NormalTexture(ResourceLocation source, int textureU, int textureV, int textureWidth, int textureHeight, int sourceWidth, int sourceHeight)
    {
        this.source = source;
        this.sourceWidth = sourceWidth;
        this.sourceHeight = sourceHeight;
        this.textureU = textureU;
        this.textureV = textureV;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
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
        graphics.blit(this.source, x, y, width, height, this.textureU, this.textureV, this.textureWidth, this.textureHeight, this.sourceWidth, this.sourceHeight);
    }
}
