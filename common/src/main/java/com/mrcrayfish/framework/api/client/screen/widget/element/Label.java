package com.mrcrayfish.framework.api.client.screen.widget.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

/**
 * Represents an abstract label element which displays text. Implementations
 * define the behaviour and appearance of the label, including the dimensions and
 * how the text is drawn.
 */
public abstract class Label
{
    /**
     * A {@link Label} with empty text, can be used as a default or fallback.
     */
    public static final Label EMPTY = constant(CommonComponents.EMPTY);

    /**
     * @return The {@link Component} representing the text of this label, may vary depending on implementation.
     */
    public abstract Component text();

    /**
     * @return The width of the label in pixel units
     */
    public abstract int width();

    /**
     * @return The height of the label in pixel units
     */
    public abstract int height();

    /**
     * Draws the label at the specified position with the given colour and shadow option.
     *
     * @param graphics a {@link GuiGraphics} instance
     * @param x        the x pos to draw the label
     * @param y        the y pos to draw the label
     * @param colour   the colour to apply to the text
     * @param shadow   true if the text should be drawn with a shadow
     */
    public abstract void draw(GuiGraphics graphics, int x, int y, int colour, boolean shadow);

    /**
     * Creates a {@link Label} with the provided text
     *
     * @param text a {@link Component} that represents the label text
     * @return a new {@link Label} with a constant supplied text
     */
    public static Label constant(Component text)
    {
        return new Impl(text);
    }

    /**
     * Creates a {@link Label} with its text provided by a {@link Supplier}
     *
     * @param text a supplier that provides the label's text as a {@link Component}
     * @return a {@link Label} instance with dynamically supplied text
     */
    public static Label dynamic(Supplier<Component> text)
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
