package com.mrcrayfish.framework.api.client.screen.widget.texture;

import com.mrcrayfish.framework.api.client.screen.widget.element.Icon;
import com.mrcrayfish.framework.api.client.screen.widget.layout.Border;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public abstract class FrameworkTexture extends Icon
{
    public abstract void draw(GuiGraphics graphics, int x, int y, int width, int height);

    @Override
    public void draw(GuiGraphics graphics, int x, int y, float partialTick)
    {
        this.draw(graphics, x, y, this.width(), this.height());
    }

    public static FrameworkTexture subImage(ResourceLocation source, int u, int v, int width, int height)
    {
        return new NormalTexture(source, u, v, width, height, 256, 256);
    }

    public static FrameworkTexture subImage(ResourceLocation source, int u, int v, int width, int height, int sourceWidth, int sourceHeight)
    {
        return new NormalTexture(source, u, v, width, height, sourceWidth, sourceHeight);
    }

    public static FrameworkTexture nineSlice(ResourceLocation source, int u, int v, int width, int height, Border border)
    {
        return new NineSliceTexture(source, u, v, width, height, border);
    }
}
