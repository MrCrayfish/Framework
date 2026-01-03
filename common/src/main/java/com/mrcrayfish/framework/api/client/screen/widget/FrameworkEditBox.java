package com.mrcrayfish.framework.api.client.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mrcrayfish.framework.api.client.screen.widget.element.Icon;
import com.mrcrayfish.framework.api.client.screen.widget.layout.Border;
import com.mrcrayfish.framework.api.client.screen.widget.layout.Padding;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.*;

/**
 * An improved version of edit boxes, with support for icons and more customisation options.
 * <p>
 * To get started, use {@link #builder()} to build out a new {@link FrameworkEditBox} instance.
 */
public final class FrameworkEditBox extends AbstractContainerWidget
{
    private static final WidgetSprites DEFAULT_SPRITES = new WidgetSprites(
        ResourceLocation.withDefaultNamespace("widget/text_field"),
        ResourceLocation.withDefaultNamespace("widget/text_field_highlighted")
    );
    public static final Padding DEFAULT_PADDING = Padding.of(4, 0, 4, 0);
    public static final Border DEFAULT_BORDER = Border.of(1);

    private final LinearLayout layout = LinearLayout.horizontal();
    private final @Nullable Icon icon;
    private final @Nullable CenteredSpriteWidget iconWidget;
    private final Border border;
    private final Padding padding;
    private final int spacing;
    private final @Nullable WidgetSprites background;
    private final @Nullable Supplier<Boolean> activeSupplier;
    private final EditBox editBox;
    private final boolean clearOnRightClick;

    private FrameworkEditBox(int x, int y, int width, int height, Function<FrameworkEditBox, Icon> icon, Padding padding, int spacing, @Nullable WidgetSprites background, Border border, String text, @Nullable String suggestion, @Nullable Component hint, @Nullable Consumer<String> callback, @Nullable Predicate<String> valueFilter, @Nullable BiFunction<String, Integer, FormattedCharSequence> styleFormatter, @Nullable Supplier<Boolean> activeSupplier, @Nullable Integer maxTextLength, boolean clearOnRightClick, @Nullable Integer iconWidthOverride)
    {
        super(x, y, width, height, CommonComponents.EMPTY);
        this.icon = icon.apply(this);
        this.padding = padding;
        this.spacing = spacing;
        this.background = background;
        this.border = border;
        this.activeSupplier = activeSupplier;
        this.clearOnRightClick = clearOnRightClick;

        // Update layout spacing
        this.layout.spacing(spacing);

        // Create the icon widget, or null if the icon function returned null
        if(this.icon != null)
        {
            int iconWidth = iconWidthOverride != null ? iconWidthOverride : this.icon.width();
            this.iconWidget = this.layout.addChild(new CenteredSpriteWidget(iconWidth, 0, this.icon), layoutSettings -> {
                layoutSettings.paddingTop(border.top() + padding.top()).paddingBottom(border.bottom() + padding.bottom()).paddingLeft(padding.left() + border.left());
            });
        }
        else
        {
            this.iconWidget = null;
        }

        // Create the custom edit box implementation
        this.editBox = this.layout.addChild(new Impl(this), layoutSettings -> {
            layoutSettings
                .paddingRight(border.right() + padding.right())
                .paddingTop(border.top() + padding.top())
                .paddingBottom(border.bottom() + padding.bottom());
            if(this.icon == null) {
                layoutSettings.paddingLeft(border.left() + padding.left());
            }
        });
        this.updateEditBoxWidth();
        if(valueFilter != null)
            this.editBox.setFilter(valueFilter);
        if(maxTextLength != null)
            this.editBox.setMaxLength(maxTextLength);
        this.editBox.setValue(text);
        this.editBox.setSuggestion(suggestion);
        if(hint != null)
            this.editBox.setHint(hint);
        if(callback != null)
            this.editBox.setResponder(callback); // Add after setting initial text to avoid call
        if(styleFormatter != null)
            this.editBox.setFormatter(styleFormatter);

        this.setSize(width, height);
    }

    private void updateEditBoxWidth()
    {
        if(this.iconWidget != null)
        {
            this.editBox.setWidth(this.getWidth() - (this.border.left() + this.padding.left() + this.iconWidget.getWidth() + this.spacing + this.padding.right() + this.border.right()));
        }
        else
        {
            this.editBox.setWidth(this.getWidth() - (this.border.left() + this.padding.left() + this.padding.right() + this.border.right()));
        }
    }

    @Override
    public void setX(int x)
    {
        super.setX(x);
        this.layout.setX(x);
    }

    @Override
    public void setY(int y)
    {
        super.setY(y);
        this.layout.setY(y);
    }

    @Override
    public void setWidth(int width)
    {
        super.setWidth(width);
        this.updateEditBoxWidth();
        this.layout.arrangeElements();
    }

    @Override
    public void setHeight(int height)
    {
        super.setHeight(height);
        this.updateEditBoxWidth();
        int contentHeight = height - (this.border.top() + this.padding.top() + this.padding.bottom() + this.border.bottom());
        this.editBox.setHeight(contentHeight);
        if(this.iconWidget != null)
            this.iconWidget.setHeight(contentHeight);
        this.layout.arrangeElements();
    }

    @Override
    public void setSize(int width, int height)
    {
        this.setWidth(width);
        this.setHeight(height);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        if(this.activeSupplier != null)
        {
            boolean active = this.activeSupplier.get();
            this.active = active;
            this.editBox.active = active;
        }
        if(this.background != null)
        {
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            graphics.setColor(1, 1, 1, this.editBox.isActive() ? 1.0F : 0.5F);
            ResourceLocation background = this.background.get(this.editBox.isActive(), this.editBox.isFocused());
            graphics.blitSprite(background, this.getX(), this.getY(), this.getWidth(), this.getHeight());
            graphics.setColor(1, 1, 1, 1);
            RenderSystem.disableBlend();
        }
        this.layout.visitWidgets(widget -> widget.render(graphics,  mouseX, mouseY, partialTick));
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output)
    {
        this.editBox.updateNarration(output);
    }

    @Override
    public List<? extends GuiEventListener> children()
    {
        return List.of(this.editBox);
    }

    @Override
    public void setFocused(boolean focused)
    {
        super.setFocused(focused);
        this.editBox.setFocused(focused);
    }

    /**
     * @return The value of the edit box
     */
    public String getText()
    {
        return this.editBox.getValue();
    }

    /**
     * @return The underlying edit box since this is just a wrapper.
     */
    public EditBox getEditBox()
    {
        return this.editBox;
    }

    /**
     * @return A {@link Builder} to build and create a {@link FrameworkEditBox}
     */
    public static Builder builder()
    {
        return new Builder();
    }

    public static class Builder
    {
        private int x;
        private int y;
        private int width = 100;
        private int height = 16;
        private Function<FrameworkEditBox, Icon> icon = editBox1 -> null;
        private Border border = DEFAULT_BORDER;
        private Padding padding = DEFAULT_PADDING;
        private int spacing = 4;
        private @Nullable WidgetSprites background = DEFAULT_SPRITES;
        private String text = "";
        private @Nullable String suggestion;
        private @Nullable Consumer<String> callback;
        private @Nullable Predicate<String> valueFilter;
        private @Nullable BiFunction<String, Integer, FormattedCharSequence> styleFormatter;
        private @Nullable Component hint;
        private @Nullable Supplier<Boolean> active;
        private @Nullable Integer maxTextLength;
        private boolean clearOnRightClick = true;
        private @Nullable Integer iconWidthOverride;

        /**
         * @return A new {@link FrameworkEditBox}
         */
        public FrameworkEditBox build()
        {
            return new FrameworkEditBox(this.x, this.y, this.width, this.height, this.icon, this.padding, this.spacing, this.background, this.border, this.text, this.suggestion, this.hint, this.callback, this.valueFilter, this.styleFormatter, this.active, this.maxTextLength, this.clearOnRightClick, this.iconWidthOverride);
        }

        /**
         * Sets the x position of this edit box.
         *
         * @param x the x position of the edit box in pixel units
         * @return this {@link Builder} for method chaining
         */
        public Builder setX(int x)
        {
            this.x = x;
            return this;
        }

        /**
         * Sets the y position of this edit box.
         *
         * @param y the y position of the edit box in pixel units
         * @return this {@link Builder} for method chaining
         */
        public Builder setY(int y)
        {
            this.y = y;
            return this;
        }

        /**
         * Sets the x and y position of the edit box. This method is simply for convenience to set
         * the x and y position in a single call.
         *
         * @param x the x position of the edit box in pixel units
         * @param y the y position of the edit box in pixel units
         * @return this {@link Builder} for method chaining
         */
        public Builder setPosition(int x, int y)
        {
            this.x = x;
            this.y = y;
            return this;
        }

        /**
         * Sets the width of this edit box.
         *
         * @param width the width of the edit box in pixel units
         * @return this {@link Builder} for method chaining
         */
        public Builder setWidth(int width)
        {
            this.width = width;
            return this;
        }

        /**
         * Sets the height of this edit box. The default height is 20 to match vanilla buttons.
         *
         * @param height the height of the edit box in pixel units
         * @return this {@link Builder} for method chaining
         */
        public Builder setHeight(int height)
        {
            this.height = height;
            return this;
        }

        /**
         * Sets the width and height (the size) of the edit box. The default height is 20 to match
         * vanilla buttons. This method is simply for convenience to set the width and height in
         * a single call.
         *
         * @param width  the width of the edit box in pixel units
         * @param height the height of the edit box in pixel units
         * @return this {@link Builder} for method chaining
         */
        public Builder setSize(int width, int height)
        {
            this.width = width;
            this.height = height;
            return this;
        }

        /**
         * Sets the icon that will be displayed on the edit box. The provided resource must be a
         * sprite, not a texture.
         *
         * @param sprite a {@link ResourceLocation} to a sprite image
         * @param width  the width of the sprite in pixels
         * @param height the height of the sprite in pixels
         * @return this {@link Builder} for method chaining
         */
        public Builder setIcon(ResourceLocation sprite, int width, int height)
        {
            this.icon = editBox -> Icon.sprite(sprite, width, height);
            return this;
        }

        /**
         * Supplies an icon that will be displayed on the edit box. Unlike {@link #setIcon(ResourceLocation, int, int)},
         * the supplier is called every frame the edit box is drawn, which allows the sprite resource
         * to dynamically change. For example, this could be used to draw a different icon depending
         * on the value in the edit box.
         *
         * @param sprite a {@link Supplier} that returns a {@link ResourceLocation} to a sprite image
         * @param width  the width of the sprite in pixels
         * @param height the height of the sprite in pixels
         * @return this {@link Builder} for method chaining
         */
        public Builder setIcon(Supplier<ResourceLocation> sprite, int width, int height)
        {
            this.icon = editBox -> Icon.sprite(sprite, width, height);
            return this;
        }

        /**
         * Similar to {@link #setIcon(Supplier, int, int)} but with the ability to reference the
         * button when creating the {@link Supplier}. Please note that the sprite function is only
         * called once to create the {@link Supplier}, unlike the created supplier which is called
         * every frame the edit box is drawn.
         *
         * @param sprite a {@link Function} that provides context of the {@link FrameworkEditBox} to
         *               aid the creation of the {@link Supplier}, which returns a {@link ResourceLocation}
         *               to a sprite image
         * @param width  the width of the sprite in pixels
         * @param height the height of the sprite in pixels
         * @return this {@link Builder} for method chaining
         */
        public Builder setIcon(Function<FrameworkEditBox, Supplier<ResourceLocation>> sprite, int width, int height)
        {
            this.icon = editBox -> Icon.sprite(sprite.apply(editBox), width, height);
            return this;
        }

        /**
         * Sets the icon that will be displayed on the edit box but allows for custom implementations
         * of {@link Icon}. Use built-in functions {@link Icon#sprite(ResourceLocation, int, int)} and
         * {@link Icon#sprite(Supplier, int, int)} to create an icon for a sprite resources, otherwise
         * custom implements can be used to draw anything.
         *
         * @param icon an {@link Icon} to be used as the icon for this edit box
         * @return this {@link Builder} for method chaining
         */
        public Builder setIcon(@Nullable Icon icon)
        {
            this.icon = editBox -> icon;
            return this;
        }

        /**
         * Similar to {@link #setIcon(Icon)} but with the ability to reference the edit box when
         * creating the {@link Icon}. Please note that the provided function is only called once
         * upon the creation of the edit box.
         *
         * @param icon a {@link Function} that returns an {@link Icon}
         * @return this {@link Builder} for method chaining
         */
        public Builder setIcon(Function<FrameworkEditBox, Icon> icon)
        {
            this.icon = icon;
            return this;
        }

        /**
         * Sets the width of the icon container. By default, the width of the icon is used. However,
         * this method can be used to make the area width smaller or larger than the icon width.
         *
         * @param width the width of the icon container in pixel units
         * @return this {@link Builder} for method chaining
         */
        public Builder setIconWidth(int width)
        {
            this.iconWidthOverride = width;
            return this;
        }

        /**
         * Sets the spacing between the icon and text
         *
         * @param spacing the spacing in pixel units
         * @return this {@link Builder} for method chaining
         */
        public Builder setSpacing(int spacing)
        {
            this.spacing = spacing;
            return this;
        }

        /**
         * Sets the padding of the container, which wraps the icon and input field
         *
         * @param padding the padding in pixel units
         * @return this {@link Builder} for method chaining
         */
        public Builder setPadding(int padding)
        {
            this.padding = Padding.of(padding, padding, padding, padding);
            return this;
        }

        /**
         * Sets the padding of the container, which wraps the icon and input field
         *
         * @param left   the left padding in pixel units
         * @param top    the top padding in pixel units
         * @param right  the right padding in pixel units
         * @param bottom the bottom padding in pixel units
         * @return this {@link Builder} for method chaining
         */
        public Builder setPadding(int left, int top, int right, int bottom)
        {
            this.padding = Padding.of(left, top, right, bottom);
            return this;
        }

        /**
         * Sets the padding of the container, which wraps the icon and input field
         *
         * @param padding a {@link Padding} object in pixel units
         * @return this {@link Builder} for method chaining
         */
        public Builder setPadding(Padding padding)
        {
            this.padding = padding;
            return this;
        }

        /**
         * Sets the background texture of the edit box.
         *
         * @param background a {@link WidgetSprites} containing the texture resources
         * @return this {@link Builder} for method chaining
         */
        public Builder setBackground(@Nullable WidgetSprites background)
        {
            this.background = background;
            return this;
        }

        /**
         * Removes the background texture of the edit box
         *
         * @return this {@link Builder} for method chaining
         */
        public Builder noBackground()
        {
            this.background = null;
            return this;
        }

        /**
         * Sets the size of the border. This will affect the layout of the icon and text. The border
         * will inset the contents, not grow the width of the widget.
         *
         * @param border the size of the border in pixel units
         * @return this {@link Builder} for method chaining
         */
        public Builder setBorder(int border)
        {
            this.border = Border.of(border);
            return this;
        }

        /**
         * Sets the size of the border. This will affect the layout of the icon and text. The border
         * will inset the contents, not grow the width of the widget.
         *
         * @param left   the left border in pixel units
         * @param top    the top border in pixel units
         * @param right  the right border in pixel units
         * @param bottom the bottom border in pixel units
         * @return this {@link Builder} for method chaining
         */
        public Builder setBorder(int left, int top, int right, int bottom)
        {
            this.border = Border.of(left, top, right, bottom);
            return this;
        }

        /**
         * Sets the size of the border. This will affect the layout of the icon and text. The border
         * will inset the contents, not grow the width of the widget.
         *
         * @param border a {@link Border} object
         * @return this {@link Builder} for method chaining
         */
        public Builder setBorder(Border border)
        {
            this.border = border;
            return this;
        }

        /**
         * Sets the initial text of the edit box. If the length of the text is greater than the
         * max length, it will be trimmed to the max length.
         *
         * @param text a {@link String} to set as the initial value of the edit box
         * @return this {@link Builder} for method chaining
         */
        public Builder setInitialText(String text)
        {
            this.text = text;
            return this;
        }

        /**
         * Sets the maximum string length of the edit box. Note that this is client side only,
         * if the value is used in any server side context, the length should be validated.
         *
         * @param maxTextLength the maximum length of an edit box string value
         * @return this {@link Builder} for method chaining
         */
        public Builder setMaxTextLength(int maxTextLength)
        {
            this.maxTextLength = maxTextLength;
            return this;
        }

        // TODO suggestions
        /*public Builder setSuggestion(@Nullable String suggestion)
        {
            this.suggestion = suggestion;
            return this;
        }*/

        /**
         * A callback to run when the edit box is updated with a new value, either from typing, deleting,
         * clearing, or any other modification.
         *
         * @param callback a {@link Consumer} which accepts a {@link String}, where the {@link String} is the new value
         * @return this {@link Builder} for method chaining
         */
        public Builder setCallback(@Nullable Consumer<String> callback)
        {
            this.callback = callback;
            return this;
        }

        /**
         * Sets a filter for the text that can be present in the edit box. For example, this could be
         * used to only allow lower case letters. The predicate is called after a key is typed, and
         * will only be set the value to the edit box if the predicate passes. This predicate will
         * also be called if a value is pasted into the edit box.
         *
         * @param valueFilter a {@link Predicate} accepting a {@link String}
         * @return this {@link Builder} for method chaining
         */
        public Builder setValueFilter(@Nullable Predicate<String> valueFilter)
        {
            this.valueFilter = valueFilter;
            return this;
        }

        /**
         * Sets the style formatter of the edit box. This can be used to apply custom styling based
         * on the format of a value. The {@link BiFunction} will be applied using the visually displayed
         * {@link String} in the edit box and the starting index of that value.
         *
         * @param styleFormatter a {@link BiFunction} that accepts a {@link String} and an integer.
         * @return this {@link Builder} for method chaining
         */
        public Builder setStyleFormatter(@Nullable BiFunction<String, Integer, FormattedCharSequence> styleFormatter)
        {
            this.styleFormatter = styleFormatter;
            return this;
        }

        /**
         * Sets the hint that will be displayed in the edit box. The hint is a {@link Component}
         * that is drawn when the edit box is empty. For example, a search box might set the hint
         * as "Search...", which gives a hint of what the edit box does.
         *
         * @param hint a {@link Component} to use as the edit box hint
         * @return this {@link Builder} for method chaining
         */
        public Builder setHint(@Nullable Component hint)
        {
            this.hint = hint;
            return this;
        }

        /**
         * Sets a dependency on an arbitrary boolean. This will update the
         * {@link AbstractWidget#active} property of the edit box.
         *
         * @param active a {@link Supplier} returning a {@link Boolean} representing the dependent state
         * @return this {@link Builder} for method chaining
         */
        public Builder setDependent(Supplier<Boolean> active)
        {
            this.active = active;
            return this;
        }

        /**
         * Sets if the edit box should clear on right click. This option is enabled by default.
         *
         * @param clearOnRightClick true if the edit should clear when right clicking
         * @return this {@link Builder} for method chaining
         */
        public Builder setClearOnRightClick(boolean clearOnRightClick)
        {
            this.clearOnRightClick = clearOnRightClick;
            return this;
        }
    }

    private static class Impl extends EditBox
    {
        private final FrameworkEditBox parent;
        private boolean drawing;

        private Impl(FrameworkEditBox parent)
        {
            super(Minecraft.getInstance().font, 0, 0, CommonComponents.EMPTY);
            this.setBordered(false);
            this.parent = parent;
        }

        @Override
        public boolean isBordered()
        {
            return false; // Hack to disable the default background
        }

        @Override
        public int getY()
        {
            if(this.drawing)
            {
                return super.getY() + (this.getHeight() - 8) / 2;
            }
            return super.getY();
        }

        @Override
        public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
        {
            this.drawing = true;
            super.renderWidget(graphics, mouseX, mouseY, partialTick);
            this.drawing = false;
        }

        @Override
        protected boolean clicked(double mouseX, double mouseY)
        {
            return this.visible && this.active && mouseX >= this.parent.getX() && mouseY >= (double) this.parent.getY() && mouseX < (double) (this.parent.getX() + this.parent.getWidth()) && mouseY < (double) (this.parent.getY() + this.parent.getHeight());
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button)
        {
            // Right-clicking will clear the edit box
            if(this.parent.clearOnRightClick && this.active && this.visible && button == 1 && this.clicked(mouseX, mouseY))
            {
                this.setValue("");
                return true;
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }
    }

    private static final class CenteredSpriteWidget extends AbstractWidget
    {
        private final Icon icon;

        public CenteredSpriteWidget(int width, int height, Icon icon)
        {
            super(0, 0, width, height, CommonComponents.EMPTY);
            this.icon = icon;
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
        {
            int iconX = this.getX() + (this.getWidth() - this.icon.width()) / 2;
            int iconY = this.getY() + (this.getHeight() - this.icon.height()) / 2;
            this.icon.draw(graphics, iconX, iconY, partialTick);
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput output) {}
    }
}
