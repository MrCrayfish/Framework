package com.mrcrayfish.framework.api.client.screen.overlay;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mrcrayfish.framework.api.client.screen.widget.layout.Margin;
import com.mrcrayfish.framework.api.client.screen.widget.layout.Padding;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Represents a base implementation for a displayable window, which can be shown on an overlayable
 * screen. The content of the window is represented by the {@link Layout} that is provided in the
 * constructor. The provided {@link Layout} is then wrapped within a {@link FrameLayout} and can
 * optionally be padded. A window comes with base customisation options, like setting the background
 * texture and applying a margin around the window.
 */
@Beta
public abstract class Window extends Overlay implements LayoutElement
{
    private final Layout layout;
    private final List<AbstractWidget> widgets;
    private final Margin outerMargin;
    private final Identifier background;

    @SuppressWarnings("unchecked")
    protected <T extends Window> Window(Function<T, Layout> content, Margin outerMargin, Padding contentPadding, Identifier background)
    {
        FrameLayout wrapper = new FrameLayout();
        if(content != null)
        {
            Layout layout = content.apply((T) this);
            wrapper.addChild(layout, s -> s.padding(contentPadding.left(), contentPadding.top(), contentPadding.right(), contentPadding.bottom()));
            wrapper.arrangeElements();
        }
        this.layout = wrapper;
        this.widgets = captureWidgets(wrapper);
        this.outerMargin = outerMargin;
        this.background = background;
    }

    public Margin getOuterMargin()
    {
        return this.outerMargin;
    }

    @Override
    public void setX(int x)
    {
        this.layout.setX(x);
    }

    @Override
    public void setY(int y)
    {
        this.layout.setY(y);
    }

    @Override
    public int getX()
    {
        return this.layout.getX();
    }

    @Override
    public int getY()
    {
        return this.layout.getY();
    }

    @Override
    public int getWidth()
    {
        return this.layout.getWidth();
    }

    @Override
    public int getHeight()
    {
        return this.layout.getHeight();
    }

    @Override
    public ScreenRectangle getInteractableArea()
    {
        return this.layout.getRectangle();
    }

    @Override
    public void visitWidgets(Consumer<AbstractWidget> consumer)
    {
        this.layout.visitWidgets(consumer);
    }

    @Override
    public final void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        if(this.background != null)
        {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, this.background, this.layout.getX(), this.layout.getY(), this.layout.getWidth(), this.layout.getHeight());
        }
        this.widgets.forEach(widget -> widget.render(graphics, mouseX, mouseY, partialTick));
    }

    @Override
    public List<? extends GuiEventListener> getWidgets()
    {
        return this.widgets;
    }

    /**
     * Adjusts the position of the window to ensure it is clamped within the specified bounds.
     * The clamping respects the defined outer margin and prevents the window from exceeding the
     * boundaries of the given {@code bounds}.
     *
     * @param bounds a {@link ScreenRectangle} representing the area within which the window must stay
     */
    protected void clampToBounds(ScreenRectangle bounds)
    {
        int x = Mth.clamp(this.getX(), this.outerMargin.left(), bounds.width() - this.getWidth() - this.outerMargin.right());
        int y = Mth.clamp(this.getY(), this.outerMargin.top(), bounds.height() - this.getHeight() - this.outerMargin.bottom());
        this.layout.setPosition(x, y);
    }

    /**
     * Captures all widgets contained within the specified {@link Layout}.
     *
     * @param layout the layout from which to gather widgets
     * @return an immutable {@link List} of all {@link AbstractWidget}s found in the layout
     */
    private static List<AbstractWidget> captureWidgets(Layout layout)
    {
        List<AbstractWidget> widgets = new ArrayList<>();
        layout.visitWidgets(widgets::add);
        return ImmutableList.copyOf(widgets);
    }

    /**
     * An abstract generic builder class designed to facilitate the creation of {@link Window} instances.
     * This class provides methods to configure common properties such as background, margin, and padding
     * while enforcing a customisable structure for subclass-specific implementations.
     *
     * @param <B> the specific builder type used to implement the builder pattern
     * @param <T> the type of {@link Window} being built
     */
    protected static abstract class Builder<B extends Builder<B, T>, T extends Window>
    {
        protected @Nullable Function<T, Layout> layout;
        protected @Nullable Identifier background;
        protected Margin outerMargin = Margin.of(5);
        protected Padding contentPadding = Padding.ZERO;

        public abstract T build();

        /**
         * Sets a {@link Layout} to be used as the content of the window
         *
         * @param layout a {@code Layout} instance that contains the content
         * @return this {@link B} for method chaining
         */
        public B setContent(Layout layout)
        {
            this.layout = ignored -> layout;
            return this.self();
        }

        /**
         * Sets a function to construct a {@link Layout} with access to an instance of {@link T}.
         * This allows for extra controls, like closing the window, which isn't possible without
         * access to the window {@link T} instance. The constructed layout will then be used as the
         * content of the window.
         *
         * @param layout a function that returns a {@code Layout} instance that contains the content
         * @return this {@link B} for method chaining
         */
        public B setContent(Function<T, Layout> layout)
        {
            this.layout = layout;
            return this.self();
        }

        /**
         * Sets the padding around the content area of the window being built.
         *
         * @param contentPadding the amount of space in pixels to apply as padding on all sides of the content
         * @return this {@link B} for method chaining
         */
        public B setContentPadding(Padding contentPadding)
        {
            this.contentPadding = contentPadding;
            return this.self();
        }

        /**
         * Sets the background sprite for the window of type {@code T}
         *
         * @param background a {@link Identifier} to a sprite resource; can be null for transparent.
         * @return this {@link B} for method chaining
         */
        public B setBackground(@Nullable Identifier background)
        {
            this.background = background;
            return this.self();
        }

        /**
         * Sets the outer margin for the window of type {@code T}. The outer margin prevents the
         * window from touching the edges of the viewport bounds.
         *
         * @param outerMargin the margin in pixels, applied around the entire window
         * @return this {@link B} for method chaining
         */
        public B setOuterMargin(Margin outerMargin)
        {
            this.outerMargin = outerMargin;
            return this.self();
        }

        /**
         * Returns this instance cast to the generic builder type.
         *
         * @return {@code this} as an instance of {@link B}
         */
        @SuppressWarnings("unchecked")
        public B self()
        {
            return (B) this;
        }
    }
}
