package com.mrcrayfish.framework.api.client.screen.widget;

import com.google.common.annotations.Beta;
import com.mrcrayfish.framework.api.client.screen.Anchor;
import com.mrcrayfish.framework.api.client.screen.overlay.impl.Dropdown;
import com.mrcrayfish.framework.api.client.screen.widget.element.Icon;
import com.mrcrayfish.framework.api.client.screen.widget.layout.Margin;
import com.mrcrayfish.framework.api.client.screen.widget.layout.Padding;
import com.mrcrayfish.framework.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Beta
public final class FrameworkSelect<T> extends AbstractWidget
{
    public static final WidgetSprites DEFAULT_SPRITES = new WidgetSprites(
        Identifier.withDefaultNamespace("widget/button"),
        Identifier.withDefaultNamespace("widget/button_disabled"),
        Identifier.withDefaultNamespace("widget/button_highlighted")
    );
    public static final Icon DEFAULT_ICON = Icon.sprite(Utils.rl("widget/select/arrow"), 8, 6);
    public static final Identifier DEFAULT_DROPDOWN_BACKGROUND = Utils.rl("widget/select/dropdown_background");
    public static final Padding DEFAULT_DROPDOWN_PADDING = Padding.of(5);
    public static final int DEFAULT_TOOLTIP_DELAY = 350;

    private static final List<Anchor> ANCHOR_ORDER = List.of(
        Anchor.BELOW_LEFT,
        Anchor.BELOW_RIGHT,
        Anchor.ABOVE_LEFT,
        Anchor.ABOVE_RIGHT,
        Anchor.END_TOP,
        Anchor.END_BOTTOM
    );

    private final Class<T> type;
    private final boolean searchable;
    private final @Nullable Anchor preferredAnchor;
    private final @Nullable WidgetSprites texture;
    private final Function<FrameworkSelect<T>, @Nullable Icon> iconFunction;
    private final @Nullable Identifier dropdownBackground;
    private final Padding dropdownPadding;
    private final @Nullable Supplier<Boolean> activeSupplier;
    private final @Nullable Function<FrameworkSelect<T>, Tooltip> tooltip;
    private final int tooltipOptions;
    private final @Nullable Consumer<Dropdown.Builder> buildDropdownListener;
    private final @Nullable Consumer<@Nullable T> callback;
    private final LinkedHashMap<String, OptionItem> options;
    private final FrameworkSelectionList list;
    private final FrameworkEditBox searchField;

    private @Nullable Dropdown dropdown;
    private @Nullable OptionItem selected;
    private @Nullable Tooltip currentTooltip;
    private boolean shiftWasDown;
    private boolean mouseIsHovering;

    private FrameworkSelect(int x, int y, int width, int height, Class<T> type, Function<T, Component> objectToLabel, List<T> values, boolean searchable, boolean allowEmpty, @Nullable T initialValue, @Nullable Consumer<@Nullable T> callback, @Nullable Anchor preferredAnchor, @Nullable WidgetSprites texture, Function<FrameworkSelect<T>, @Nullable Icon> iconFunction, @Nullable Identifier dropdownBackground, Padding dropdownPadding, int dropdownMinWidth, int visibleListItems, @Nullable Supplier<Boolean> active, @Nullable Function<FrameworkSelect<T>, Tooltip> tooltip, int tooltipDelay, int tooltipOptions, @Nullable Consumer<FrameworkSelectionList.Builder> buildOptionListListener, @Nullable Consumer<FrameworkEditBox.Builder> buildSearchFieldListener, @Nullable Consumer<Dropdown.Builder> buildDropdownListener)
    {
        super(x, y, width, height, CommonComponents.EMPTY);
        this.type = type;
        this.searchable = searchable;
        this.callback = callback;
        this.preferredAnchor = preferredAnchor;
        this.texture = texture;
        this.iconFunction = iconFunction;
        this.dropdownBackground = dropdownBackground;
        this.dropdownPadding = dropdownPadding;
        this.activeSupplier = active;
        this.tooltip = tooltip;
        this.tooltipOptions = tooltipOptions;
        this.buildDropdownListener = buildDropdownListener;

        // Build the select options
        Stream<OptionItem> stream = Stream.concat(
            allowEmpty ? Stream.of(new OptionItem(null, Component.literal(""))) : Stream.empty(),
            values.stream().map(t -> new OptionItem(t, objectToLabel.apply(t)))
        );
        this.options = stream.collect(Collectors.toMap(OptionItem::getKey, Function.identity(), (a, b) -> a, LinkedHashMap::new));

        var horizontalDropdownPadding = dropdownPadding.left() + dropdownPadding.right();
        int widgetWidth = Math.max(width - horizontalDropdownPadding, dropdownMinWidth - horizontalDropdownPadding);

        var listBuilder = FrameworkSelectionList.builder()
            .setSize(widgetWidth, 16 * visibleListItems)
            .setListPadding(Padding.ZERO)
            .setScrollBarSpacing(4)
            .setItemHeight(16);
        if(buildOptionListListener != null)
            buildOptionListListener.accept(listBuilder);
        this.list = listBuilder.build();
        this.list.replaceItems(this.options.values().stream().map(item -> (FrameworkSelectionList.Item) item).toList());

        var searchFieldBuilder = FrameworkEditBox.builder()
            .setSize(widgetWidth, 16)
            .setPadding(2, 0, 2, 0)
            .setIcon(Identifier.withDefaultNamespace("icon/search"), 12, 12)
            .setCallback(s -> {
                List<FrameworkSelectionList.Item> newItems = this.options.entrySet().stream().filter(entry -> {
                    return entry.getKey().toLowerCase(Locale.ROOT).contains(s.toLowerCase(Locale.ROOT));
                }).map(Map.Entry::getValue).map(item -> (FrameworkSelectionList.Item) item).toList();
                this.list.replaceItems(newItems);
            });
        if(buildSearchFieldListener != null)
            buildSearchFieldListener.accept(searchFieldBuilder);
        this.searchField = searchFieldBuilder.build();

        // Try and set the initial value (as long as it's a valid value)
        if(initialValue != null)
        {
            this.options.values().stream()
                .filter(item -> initialValue.equals(item.value))
                .findAny()
                .ifPresent(item -> this.selected = item);
        }

        // If the select doesn't allow empty values, try and select the first possible value in the options
        if(!allowEmpty && this.selected == null)
        {
            // Use the first option as the selected option
            this.selected = Optional.ofNullable(this.options.firstEntry()).map(Map.Entry::getValue).orElse(null);
        }

        // Validate that a value was selected if select doesn't allow empty. This can only happen if
        // the select wasn't configured with any options in the Builder class.
        if(!allowEmpty && this.selected == null)
            throw new IllegalArgumentException("There must be at least one option when allowEmpty is false");

        this.updateActiveState();
        this.rebuildTooltip();
        this.setTooltipDelay(Duration.ofMillis(tooltipDelay));
    }

    /**
     * Retrieves the type of the selectable options
     *
     * @return the class instance of the generic parameter
     */
    public Class<T> getType()
    {
        return this.type;
    }

    /**
     * Sets the selected value for this select. The value must exist in the valid options, as defined
     * when building the select.
     *
     * @param selected the value to be set as selected.
     */
    public void setSelected(T selected)
    {
        for(OptionItem option : this.options.values())
        {
            if(option.value == selected)
            {
                this.selected = option;
                this.triggerCallback();
                break;
            }
        }
    }

    /**
     * Directly sets the selected item for this select. The provided option item must exist in the
     * valid options, as defined when building the select.
     *
     * @param item the option item that represents the value to be selected
     */
    private void setSelected(OptionItem item)
    {
        if(this.options.containsValue(item))
        {
            this.selected = item;
            this.triggerCallback();
        }
    }

    /**
     * Invokes the callback function associated with the selected value.
     */
    private void triggerCallback()
    {
        if(this.callback != null && this.selected != null)
        {
            this.callback.accept(this.selected.value);
        }
    }

    /**
     * Retrieves the currently selected value. If this select allows an empty value, the returned
     * value may be {@code null}.
     *
     * @return the selected value of type {@code T}, or {@code null} if no item is selected or available.
     */
    @Nullable
    public T getSelected()
    {
        return this.selected != null ? this.selected.value : null;
    }

    private void updateActiveState()
    {
        if(this.activeSupplier != null)
        {
            this.active = this.activeSupplier.get();
        }
    }

    /**
     * Call to manually rebuild the tooltip
     */
    public void rebuildTooltip()
    {
        if(this.tooltip != null)
        {
            this.currentTooltip = this.tooltip.apply(this);
        }
    }

    private void updateTooltip()
    {
        if((this.tooltipOptions & TooltipOptions.REBUILD_TOOLTIP_ON_SHIFT) != 0)
        {
            if(!this.shiftWasDown && Minecraft.getInstance().hasShiftDown() && this.isHovered())
            {
                this.rebuildTooltip();
                this.shiftWasDown = true;
            }
        }
        if(this.shiftWasDown && !Minecraft.getInstance().hasShiftDown())
        {
            this.rebuildTooltip();
            this.shiftWasDown = false;
        }

        if((this.tooltipOptions & TooltipOptions.REBUILD_TOOLTIP_ON_WIDGET_HOVER) != 0)
        {
            if(this.isHovered())
            {
                if(!this.mouseIsHovering)
                {
                    this.rebuildTooltip();
                    this.mouseIsHovering = true;
                }
            }
            else
            {
                this.mouseIsHovering = false;
            }
        }

        if(!this.active && (this.tooltipOptions & TooltipOptions.DISABLE_TOOLTIP_WHEN_WIDGET_INACTIVE) != 0)
        {
            this.setTooltip(null);
        }
        else
        {
            this.setTooltip(this.currentTooltip);
        }
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick)
    {
        this.updateActiveState();
        this.updateTooltip();
        if(this.texture != null)
        {
            float alpha = this.active ? 1.0F : 0.75F;
            extractor.blitSprite(RenderPipelines.GUI_TEXTURED, this.texture.get(this.active, this.isHoveredOrFocused() && this.active), this.getX(), this.getY(), this.getWidth(), this.getHeight(), alpha);
        }
        int textEnd = -1;
        if(this.iconFunction != null)
        {
            Icon icon = this.iconFunction.apply(this);
            if(icon != null)
            {
                int margin = Math.max((this.getHeight() - icon.height()) / 2, 0);
                int iconX = this.getX() + this.getWidth() - icon.width() - margin;
                int iconY = this.getY() + margin;
                int alpha = ARGB.white(this.active ? 1.0F : 0.5F);
                icon.draw(extractor, iconX, iconY, alpha, partialTick);
                textEnd = this.getWidth() - icon.width() - margin;
            }
        }
        if(this.selected != null)
        {
            int margin = Math.max((this.getHeight() - 8) / 2, 0);
            int maxWidth = (textEnd == -1 ? this.getWidth() - margin: textEnd - 4) - margin;
            Font font = Minecraft.getInstance().font;
            String label = this.selected.label.getString();
            if(font.width(label) > maxWidth)
            {
                label = font.plainSubstrByWidth(label, maxWidth - font.width("...")) + "...";
            }
            extractor.text(Minecraft.getInstance().font, label, this.getX() + margin, this.getY() + margin, 0xFFFFFFFF);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {}

    @Override
    public void onClick(MouseButtonEvent event, boolean doubleClick)
    {
        if(this.dropdown != null)
            this.dropdown.close();
        var dropdownBuilder = Dropdown.builder()
            .setBackground(this.dropdownBackground)
            .setContentPadding(this.dropdownPadding)
            .setContent(dropdown -> {
                LinearLayout root = LinearLayout.vertical().spacing(4);
                if(this.searchable) {
                    root.addChild(this.searchField);
                }
                root.addChild(this.list);
                return root;
            });
        if(this.buildDropdownListener != null)
            this.buildDropdownListener.accept(dropdownBuilder);
        this.dropdown = dropdownBuilder.build();
        this.dropdown.show(this::updatePosition);
        this.list.scrollToItem(this.selected);
    }

    private void updatePosition(ScreenRectangle screenArea)
    {
        if(this.dropdown == null)
            return;

        Margin outerMargin = this.dropdown.getOuterMargin();
        ScreenRectangle validArea = new ScreenRectangle(
            screenArea.left() + outerMargin.left(),
            screenArea.top() + outerMargin.top(),
            screenArea.width() - (outerMargin.left() + outerMargin.right()),
            screenArea.height() - (outerMargin.top() + outerMargin.bottom())
        );

        if(this.preferredAnchor != null && this.applyAnchor(this.dropdown, this.preferredAnchor, validArea))
            return;

        for(Anchor anchor : ANCHOR_ORDER)
        {
            if(anchor != this.preferredAnchor)
            {
                if(this.applyAnchor(this.dropdown, anchor, validArea))
                {
                    return;
                }
            }
        }

        // Fallback to centering in the middle of the screen
        Anchor.CENTERED.apply(this.dropdown, screenArea);
    }

    private boolean applyAnchor(Dropdown dropdown, Anchor anchor, ScreenRectangle validArea)
    {
        anchor.apply(dropdown, this);
        return this.isRectangleWithin(dropdown.getRectangle(), validArea);
    }

    private boolean isRectangleWithin(ScreenRectangle inner, ScreenRectangle outer)
    {
        return inner.left() >= outer.left() && inner.top() >= outer.top() && inner.right() <= outer.right() && inner.bottom() <= outer.bottom();
    }

    private class OptionItem extends FrameworkSelectionList.Item
    {
        private final String key;
        private final Component label;
        private final @Nullable T value;

        public OptionItem(@Nullable T value, Component label)
        {
            this.key = label.getString();
            this.label = label;
            this.value = value;
        }

        public String getKey()
        {
            return this.key;
        }

        @Override
        public boolean isSelectable()
        {
            return false;
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick)
        {
            if(FrameworkSelect.this.dropdown != null)
            {
                FrameworkSelect.this.setSelected(this);
                FrameworkSelect.this.dropdown.close();
                return true;
            }
            return false;
        }

        @Override
        protected void extractContent(GuiGraphicsExtractor extractor, int mouseX, int mouseY, boolean hovered, boolean selected, float partialTick)
        {
            extractor.centeredText(Minecraft.getInstance().font, this.label, this.getX() + this.getWidth() / 2, this.getY() + 4, 0xFFFFFFFF);
        }

        @Override
        protected void extractBackground(FrameworkSelectionList.@Nullable ItemSprites sprites, GuiGraphicsExtractor extractor, int mouseX, int mouseY, boolean hovered, boolean selected)
        {
            selected = (FrameworkSelect.this.selected == this) || hovered;
            super.extractBackground(sprites, extractor, mouseX, mouseY, hovered, selected);
        }
    }

    public static <T> Builder<T> builder(Class<T> type, Function<T, Component> valueToLabel)
    {
        return new Builder<>(type, valueToLabel);
    }

    public static class Builder<T>
    {
        private final Class<T> type;
        private final Function<T, Component> valueToLabel;
        private Supplier<List<T>> values = ArrayList::new;
        private int x;
        private int y;
        private int width = 50;
        private int height = 20;
        private boolean allowEmpty;
        private boolean searchable = true;
        private @Nullable T initialValue;
        private @Nullable Consumer<@Nullable T> callback;
        private @Nullable Anchor preferredAnchor;
        private @Nullable WidgetSprites buttonTexture = DEFAULT_SPRITES;
        private Function<FrameworkSelect<T>, @Nullable Icon> iconFunction = btn -> DEFAULT_ICON;
        private @Nullable Identifier dropdownBackground = DEFAULT_DROPDOWN_BACKGROUND;
        private Padding dropdownPadding = DEFAULT_DROPDOWN_PADDING;
        private int dropdownMinWidth;
        private int visibleListItems = 6;
        private @Nullable Supplier<Boolean> active;
        private @Nullable Function<FrameworkSelect<T>, Tooltip> tooltip;
        private int tooltipDelay = DEFAULT_TOOLTIP_DELAY;
        private int tooltipOptions;
        private @Nullable Consumer<FrameworkSelectionList.Builder> buildOptionListListener;
        private @Nullable Consumer<FrameworkEditBox.Builder> buildSearchFieldListener;
        private @Nullable Consumer<Dropdown.Builder> buildDropdownListener;

        private Builder(Class<T> type, Function<T, Component> valueToLabel)
        {
            this.type = type;
            this.valueToLabel = valueToLabel;
        }

        public FrameworkSelect<T> build()
        {
            return new FrameworkSelect<>(this.x, this.y, this.width, this.height, this.type, this.valueToLabel, this.values.get(), this.searchable, this.allowEmpty, this.initialValue, this.callback, this.preferredAnchor, this.buttonTexture, this.iconFunction, this.dropdownBackground, this.dropdownPadding, this.dropdownMinWidth, this.visibleListItems, this.active, this.tooltip, this.tooltipDelay, this.tooltipOptions, this.buildOptionListListener, this.buildSearchFieldListener, this.buildDropdownListener);
        }

        /**
         * Sets the x position of this select.
         *
         * @param x the x position of the select in pixel units
         * @return this {@link Builder} for method chaining
         */
        public Builder<T> setX(int x)
        {
            this.x = x;
            return this;
        }

        /**
         * Sets the y position of this select.
         *
         * @param y the y position of the select in pixel units
         * @return this {@link Builder} for method chaining
         */
        public Builder<T> setY(int y)
        {
            this.y = y;
            return this;
        }

        /**
         * Sets the x and y position of the select. This method is simply for convenience to set
         * the x and y position in a single call.
         *
         * @param x the x position of the select in pixel units
         * @param y the y position of the select in pixel units
         * @return this {@link Builder} for method chaining
         */
        public Builder<T> setPosition(int x, int y)
        {
            this.x = x;
            this.y = y;
            return this;
        }

        /**
         * Sets the width of this select.
         *
         * @param width the width of the select in pixel units
         * @return this {@link Builder} for method chaining
         */
        public Builder<T> setWidth(int width)
        {
            this.width = width;
            return this;
        }

        /**
         * Sets the height of this select. The default height is 20 to match vanilla buttons.
         *
         * @param height the height of the select in pixel units
         * @return this {@link Builder} for method chaining
         */
        public Builder<T> setHeight(int height)
        {
            this.height = height;
            return this;
        }

        /**
         * Sets the width and height (the size) of the select. The default height is 20 to match
         * vanilla buttons. This method is simply for convenience to set the width and height in
         * a single call.
         *
         * @param width  the width of the select in pixel units
         * @param height the height of the select in pixel units
         * @return this {@link Builder} for method chaining
         */
        public Builder<T> setSize(int width, int height)
        {
            this.width = width;
            this.height = height;
            return this;
        }

        /**
         * Sets the supplier that provides a list of options for the select
         *
         * @param values the supplier that provides a list of values
         * @return this {@link Builder} for method chaining
         */
        public Builder<T> setValues(Supplier<List<T>> values)
        {
            this.values = values;
            return this;
        }

        /**
         * Sets whether the select should allow an empty value. This means the select allows no
         * option to be selected.
         *
         * @param allowEmpty a boolean indicating if empty values are allowed.
         *                   If true, the configuration may accept empty values;
         *                   otherwise, empty values will be disallowed.
         * @return the current instance of the Builder for chaining further method calls.
         */
        public Builder<T> setAllowEmpty(boolean allowEmpty)
        {
            this.allowEmpty = allowEmpty;
            return this;
        }

        /**
         * Sets whether the list of available options can be searched. This will add a search field
         * in the dropdown menu. Selects are searchable by default.
         *
         * @param searchable {@code true} if the select should be searchable, {@code false} to disable.
         * @return this {@link Builder} instance for method chaining
         */
        public Builder<T> setSearchable(boolean searchable)
        {
            this.searchable = searchable;
            return this;
        }

        /**
         * Sets the initial value for the select.
         *
         * @param initialValue the initial value to be set
         * @return this {@link Builder} instance for method chaining
         */
        public Builder<T> setInitialValue(T initialValue)
        {
            this.initialValue = initialValue;
            return this;
        }

        /**
         * Sets the callback when the selected value of the select is updated
         *
         * @param callback the callback to invoke, or {@code null} if no callback is required
         * @return this {@link Builder} instance for method chaining
         */
        public Builder<T> setCallback(@Nullable Consumer<@Nullable T> callback)
        {
            this.callback = callback;
            return this;
        }

        /**
         * Sets the preferred anchor for the select's dropdown. If the preferred anchor causes the
         * dropdown to be positioned in an invalid area, the select will fall back to the {@link #ANCHOR_ORDER}
         * list.
         *
         * @param anchor the anchor to be set as the preferred anchor, or {@code null} if no specific anchor is preferred
         * @return this {@link Builder} instance for method chaining
         */
        public Builder<T> setPreferredAnchor(@Nullable Anchor anchor)
        {
            this.preferredAnchor = anchor;
            return this;
        }

        /**
         * Sets the texture to be used for the button of the select.
         *
         * @param sprites a {@link WidgetSprites} containing the resource locations to textures. Can
         *                be {@code null} to remove the texture; the button will then be full transparent.
         * @return this {@link Builder} instance for method chaining
         */
        public Builder<T> setButtonTexture(@Nullable WidgetSprites sprites)
        {
            this.buttonTexture = sprites;
            return this;
        }

        /**
         * Removes the texture from the button of the select, making the button fully transparent.
         *
         * @return this {@link Builder} instance for method chaining
         */
        public Builder<T> noButtonTexture()
        {
            this.buttonTexture = null;
            return this;
        }

        /**
         * Sets the icon to be displayed on the button of the select. Changing this will replace
         * the "down arrow" on the right side of the button.
         *
         * @param icon a {@link Function} that takes a {@link FrameworkSelect} instance
         *             and returns an {@link Icon} to be used for the button
         * @return this {@link Builder} instance for method chaining
         */
        public Builder<T> setButtonIcon(Function<FrameworkSelect<T>, Icon> icon)
        {
            this.iconFunction = icon;
            return this;
        }

        /**
         * Removes the icon from the button of the select
         *
         * @return this {@link Builder} instance for method chaining
         */
        public Builder<T> noButtonIcon()
        {
            this.iconFunction = select -> null;
            return this;
        }

        /**
         * Convenience method to set the texture of the dropdown background. For full customisation
         * of the dropdown, see {@link #setOnBuildDropdownListener(Consumer)} to get access to the
         * dropdown builder.
         *
         * @param sprite a {@link Identifier} to a nine-slice sprite, or {@code null} for a full transparent background
         * @return this {@link Builder} instance for method chaining
         */
        public Builder<T> setDropdownBackground(@Nullable Identifier sprite)
        {
            this.dropdownBackground = sprite;
            return this;
        }

        /**
         * Convenience method to set the padding of the dropdown content. For full customisation
         * of the dropdown, see {@link #setOnBuildDropdownListener(Consumer)} to get access to the
         * dropdown builder.
         *
         * @param padding a {@link Padding} containing the padding values
         * @return this {@link Builder} instance for method chaining
         */
        public Builder<T> setDropdownPadding(Padding padding)
        {
            this.dropdownPadding = padding;
            return this;
        }

        /**
         * Sets the minimum width of the select's dropdown. The width of the dropdown is determined
         * by either the min width or the width of the select's button, whichever one is greater; so
         * if the min width is less than the button width, this option will have no effect.
         *
         * @param width the minimum width of the dropdown
         * @return this {@link Builder} instance for method chaining
         */
        public Builder<T> setDropdownMinWidth(int width)
        {
            this.dropdownMinWidth = width;
            return this;
        }

        /**
         * Sets the number of visible items in the option list. The default value is 6.
         *
         * @param visibleListItems the number of list items
         * @return this {@link Builder} instance for method chaining
         */
        public Builder<T> setVisibleListItems(int visibleListItems)
        {
            this.visibleListItems = visibleListItems;
            return this;
        }

        /**
         * Sets a dependency on an arbitrary boolean. This will update the
         * {@link AbstractWidget#active} property of the button.
         *
         * @param active a {@link Supplier} returning a {@link Boolean} representing the dependent state
         * @return this {@link Builder} instance for method chaining
         */
        public Builder<T> setDependent(@Nullable Supplier<Boolean> active)
        {
            this.active = active;
            return this;
        }

        /**
         * Sets the tooltip for the select. See {@link #setTooltip(Function)} for more customisation.
         *
         * @param tooltip a {@link Tooltip} object containing the tooltip
         * @return this {@link Builder} for method chaining
         */
        public Builder<T> setTooltip(Tooltip tooltip)
        {
            this.tooltip = btn -> tooltip;
            return this;
        }

        /**
         * Sets the tooltip for the select. Please note that {@link FrameworkSelect} will call the
         * provided function everytime if the tooltip cache is invalid. The cache by default is only
         * invalidated when the button is clicked. It is very common in mods to show additional
         * information only when the shift key is pressed, and by adding
         * {@link TooltipOptions#REBUILD_TOOLTIP_ON_SHIFT} via {@link #setTooltipOptions(int, int[])} will
         * additionally invalidate the cache when the shift key is pressed or released.
         *
         * @param tooltip a {@link Function} that returns a {@link Tooltip}
         * @return this {@link Builder} for method chaining
         */
        public Builder<T> setTooltip(@Nullable Function<FrameworkSelect<T>, Tooltip> tooltip)
        {
            this.tooltip = tooltip;
            return this;
        }

        /**
         * Sets the delay (in milliseconds) before the tooltip is shown
         *
         * @param delay the time in milliseconds
         * @return this {@link Builder} for method chaining
         */
        public Builder<T> setTooltipDelay(int delay)
        {
            this.tooltipDelay = delay;
            return this;
        }

        /**
         * Sets the tooltip options for the select. Tooltip options give additional control over
         * how tooltips behave. All possible tooltip options can be found at {@link TooltipOptions}.
         *
         * @param option  an option from {@link TooltipOptions}
         * @param options additional options from {@link TooltipOptions}
         * @return this {@link Builder} for method chaining
         */
        public Builder<T> setTooltipOptions(int option, int ... options)
        {
            for(int flag : options) option |= flag;
            this.tooltipOptions = option;
            return this;
        }

        /**
         * Set a listener to apply extra customisation of the option list.
         *
         * @param listener a consumer that accepts a {@link FrameworkSelectionList.Builder} to customise the option list.
         * @return this {@link Builder} for method chaining
         */
        public Builder<T> setOnBuildOptionListListener(Consumer<FrameworkSelectionList.Builder> listener)
        {
            this.buildOptionListListener = listener;
            return this;
        }

        /**
         * Set a listener to apply extra customisation of the search field.
         *
         * @param listener a consumer that accepts a {@link FrameworkEditBox.Builder} to customise the search field.
         * @return this {@link Builder} for method chaining
         */
        public Builder<T> setOnBuildSearchFieldListener(Consumer<FrameworkEditBox.Builder> listener)
        {
            this.buildSearchFieldListener = listener;
            return this;
        }

        /**
         * Set a listener to apply extra customisation of the dropdown.
         *
         * @param listener a consumer that accepts a {@link Dropdown.Builder} to customise the dropdown.
         * @return this {@link Builder} for method chaining
         */
        public Builder<T> setOnBuildDropdownListener(Consumer<Dropdown.Builder> listener)
        {
            this.buildDropdownListener = listener;
            return this;
        }
    }
}
