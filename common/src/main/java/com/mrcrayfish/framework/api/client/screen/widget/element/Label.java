package com.mrcrayfish.framework.api.client.screen.widget.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

public abstract class Label
{
    public static final Label EMPTY = create(CommonComponents.EMPTY);

    public abstract Component text();

    public abstract int width();

    public abstract int height();

    public abstract void draw(GuiGraphics graphics, int x, int y, int colour, boolean shadow);

    public static Label create(Component text)
    {
        return new Impl(text);
    }

    public static Label create(Supplier<Component> text)
    {
        return new Impl(text);
    }

    private static final class Impl extends Label
    {
        private final Supplier<Component> text;
        private final Font font;

        private Impl(Component text)
        {
            this(() -> text);
        }

        private Impl(Supplier<Component> text)
        {
            this.text = text;
            this.font = Minecraft.getInstance().font;
        }

        @Override
        public Component text()
        {
            return this.text.get();
        }

        @Override
        public int width()
        {
            return this.font.width(this.text.get());
        }

        @Override
        public int height()
        {
            return this.font.lineHeight;
        }

        @Override
        public void draw(GuiGraphics graphics, int x, int y, int colour, boolean shadow)
        {
            graphics.drawString(this.font, this.text.get(), x, y, colour, shadow);
        }
    }
}
