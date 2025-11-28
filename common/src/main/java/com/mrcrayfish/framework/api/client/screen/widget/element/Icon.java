package com.mrcrayfish.framework.api.client.screen.widget.element;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public abstract class Icon
{
    public abstract int width();

    public abstract int height();

    public abstract void draw(GuiGraphics graphics, int x, int y, float partialTick);

    public static Icon sprite(ResourceLocation resource, int width, int height)
    {
        return new Sprite(resource, width, height);
    }

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
