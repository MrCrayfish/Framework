package com.mrcrayfish.framework.api.client.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mrcrayfish.framework.Constants;
import com.mrcrayfish.framework.api.client.screen.widget.element.Icon;
import com.mrcrayfish.framework.api.client.screen.widget.element.Label;
import com.mrcrayfish.framework.api.client.screen.widget.element.Sound;
import com.mrcrayfish.framework.api.client.screen.widget.input.Action;
import com.mrcrayfish.framework.api.client.screen.widget.input.MouseInput;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.EnumMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * An improved version of buttons, with support for icons, better tooltips, change the background
 * texture, handle actions for every mouse button, and custom content rendering.
 * <p>
 * To get started, use {@link #builder()} to build out a new {@link FrameworkButton} instance.
 */
public final class FrameworkButton extends AbstractButton
{
    /**
     * The default sprites used for Framework buttons. This is just vanilla button textures.
     */
    public static final WidgetSprites DEFAULT_SPRITES = new WidgetSprites(
        ResourceLocation.withDefaultNamespace("widget/button"),
        ResourceLocation.withDefaultNamespace("widget/button_disabled"),
        ResourceLocation.withDefaultNamespace("widget/button_highlighted")
    );

    /**
     * The default tooltip delay for Framework buttons. A good happy medium (opinionated)
     */
    public static final int DEFAULT_TOOLTIP_DELAY = 350;

    /**
     * The default content renderer used by Framework buttons. This content renderer will draw the
     * icon and/or label provided to the button and align the content horizontally center.
     */
    public static final ContentRenderer<FrameworkButton> DEFAULT_CONTENT_RENDERER = new DefaultContentRenderer();

    private final Label label;
    private final @Nullable Icon icon;
    private final int spacing;
    private final @Nullable EnumMap<MouseInput, Action<FrameworkButton>> actions;
    private final @Nullable Supplier<WidgetSprites> texture;
    private final @Nullable Supplier<Boolean> activeSupplier;
    private final @Nullable Function<FrameworkButton, Tooltip> tooltip;
    private final int tooltipOptions;
    private @Nullable Tooltip currentTooltip;
    private boolean shiftWasDown;
    private boolean mouseIsHovering;
    private final @Nullable ContentRenderer<FrameworkButton> contentRenderer;

    private FrameworkButton(int x, int y, int width, int height, Label label, Function<FrameworkButton, Icon> icon, int spacing, @Nullable EnumMap<MouseInput, Action<FrameworkButton>> actions, @Nullable Supplier<WidgetSprites> texture, @Nullable Supplier<Boolean> activeSupplier, @Nullable Function<FrameworkButton, Tooltip> tooltip, int tooltipDelay, int tooltipOptions, @Nullable ContentRenderer<FrameworkButton> contentRenderer)
    {
        super(x, y, width, height, CommonComponents.EMPTY);
        this.label = label;
        this.icon = icon.apply(this);
        this.spacing = spacing;
        this.actions = actions;
        this.texture = texture;
        this.activeSupplier = activeSupplier;
        this.tooltip = tooltip;
        this.tooltipOptions = tooltipOptions;
        this.contentRenderer = contentRenderer;
        this.updateActiveState();
        this.rebuildTooltip();
        this.setTooltipDelay(Duration.ofMillis(tooltipDelay));
    }

    /**
     * A Component representing the message (the label) that is drawn on the button. Note that
     *
     * @return A Component representing the message (the label) that is drawn on the button
     */
    @Override
    public Component getMessage()
    {
        return this.label.text();
    }

    /**
     * The {@link Label} used by this button. Since the label of the button can be dynamic, using
     * {@link #getMessage()} will return a Component that represents the label returned by the
     * dynamic supplier at the time of calling the method. If this button does not have Label, this
     * method will return {@link Label#EMPTY} instead.
     *
     * @return The {@link Label} used for this button.
     */
    public Label getLabel()
    {
        return this.label;
    }

    /**
     * @return The Icon to draw on the button or null if not set
     */
    @Nullable
    public Icon getIcon()
    {
        return this.icon;
    }

    /**
     * @return The sprite textures used when drawing the button or null if no textures are set
     */
    @Nullable
    public WidgetSprites getTexture()
    {
        return this.texture != null ? this.texture.get() : null;
    }

    /**
     * @return The pixel spacing used between the icon and label of the button
     */
    public int getSpacing()
    {
        return this.spacing;
    }

    private void onAction(int button)
    {
        Action<FrameworkButton> action = this.actions != null ? this.actions.get(MouseInput.fromButton(button)) : null;
        if(action != null)
        {
            action.handler().accept(this);
            Sound sound = action.sound();
            if(sound != null)
            {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sound.value(), sound.pitch(), sound.volume()));
            }
        }
        else
        {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
        this.rebuildTooltip();
    }

    private void updateActiveState()
    {
        if(this.activeSupplier != null)
        {
            this.active = this.activeSupplier.get();
        }
    }

    /**
     * Call to manually rebuild the button tooltip
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
            if(!this.shiftWasDown && Screen.hasShiftDown() && this.isHovered())
            {
                this.rebuildTooltip();
                this.shiftWasDown = true;
            }
        }
        if(this.shiftWasDown && !Screen.hasShiftDown())
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
    public void onPress()
    {
        this.onAction(0);
    }

    @Override
    public void playDownSound(SoundManager manager) {}

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        this.updateActiveState();
        this.updateTooltip();
        if(this.contentRenderer != null)
        {
            this.contentRenderer.draw(this, graphics, mouseX, mouseY, partialTick);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output)
    {
        this.defaultButtonNarrationText(output);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if(this.active && this.visible && this.isValidClickButton(button) && this.clicked(mouseX, mouseY))
        {
            this.onAction(button);
            return true;
        }
        return false;
    }

    @Override
    protected boolean isValidClickButton(int button)
    {
        return button == 0 || this.actions != null && this.actions.containsKey(MouseInput.fromButton(button));
    }

    /**
     * @return A {@link Builder} to build and create a {@link FrameworkButton}
     */
    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private int x;
        private int y;
        private int width = 20;
        private int height = 20;
        private Label label = Label.EMPTY;
        private Function<FrameworkButton, Icon> icon = btn -> null;
        private int spacing = 4;
        private @Nullable EnumMap<MouseInput, Action<FrameworkButton>> actions;
        private @Nullable Supplier<WidgetSprites> texture = () -> DEFAULT_SPRITES;
        private @Nullable Supplier<Boolean> active;
        private @Nullable Function<FrameworkButton, Tooltip> tooltip;
        private int tooltipDelay = DEFAULT_TOOLTIP_DELAY;
        private int tooltipOptions;
        private @Nullable ContentRenderer<FrameworkButton> contentRenderer = FrameworkButton.DEFAULT_CONTENT_RENDERER;

        private Builder() {}

        public FrameworkButton build()
        {
            return new FrameworkButton(this.x, this.y, this.width, this.height, this.label, this.icon, this.spacing, this.actions, this.texture, this.active, this.tooltip, this.tooltipDelay, this.tooltipOptions, this.contentRenderer);
        }

        private EnumMap<MouseInput, Action<FrameworkButton>> actions()
        {
            if(this.actions == null)
            {
                this.actions = new EnumMap<>(MouseInput.class);
            }
            return this.actions;
        }

        /**
         * Sets the x position of this button.
         *
         * @param x the x position of the button in pixel units
         * @return this {@link Builder} for method chaining
         */
        public Builder setX(int x)
        {
            this.x = x;
            return this;
        }

        /**
         * Sets the y position of this button.
         *
         * @param y the y position of the button in pixel units
         * @return this {@link Builder} for method chaining
         */
        public Builder setY(int y)
        {
            this.y = y;
            return this;
        }

        /**
         * Sets the x and y position of the button. This method is simply for convenience to set the
         * x and y position in a single call.
         *
         * @param x the x position of the button in pixel units
         * @param y the y position of the button in pixel units
         * @return this {@link Builder} for method chaining
         */
        public Builder setPosition(int x, int y)
        {
            this.x = x;
            this.y = y;
            return this;
        }

        /**
         * Sets the width of this button.
         *
         * @param width the width of the button in pixel units
         * @return this {@link Builder} for method chaining
         */
        public Builder setWidth(int width)
        {
            this.width = width;
            return this;
        }

        /**
         * Sets the height of this button. The default height is 20 to match vanilla buttons.
         *
         * @param height the height of the button in pixel units
         * @return this {@link Builder} for method chaining
         */
        public Builder setHeight(int height)
        {
            this.height = height;
            return this;
        }

        /**
         * Sets the width and height (the size) of the button. The default height is 20 to match
         * vanilla buttons. This method is simply for convenience to set the width and height in
         * a single call.
         *
         * @param width  the width of the button in pixel units
         * @param height the height of the button in pixel units
         * @return this {@link Builder} for method chaining
         */
        public Builder setSize(int width, int height)
        {
            this.width = width;
            this.height = height;
            return this;
        }

        /**
         * Sets the label that will be displayed on the button.
         *
         * @param text a {@link Component} to use for the label
         * @return this {@link Builder} for method chaining
         */
        public Builder setLabel(Component text)
        {
            this.label = Label.constant(text);
            return this;
        }

        /**
         * Supplies a label that will be displayed on the button. Please note that the value
         * supplied is not cached by the button and is called every frame the button is drawn.
         *
         * @param supplier a {@link Supplier} that returns a {@link Component} to use for the label
         * @return this {@link Builder} for method chaining
         */
        public Builder setLabel(Supplier<Component> supplier)
        {
            this.label = Label.dynamic(supplier);
            return this;
        }

        /**
         * Sets the {@link Label} that will be displayed on the button. Unlike {@link #setLabel(Component)}
         * and {@link #setLabel(Supplier)}, this method allows for a custom implementation of {@link Label}
         * to be used.
         *
         * @param label the
         * @return this {@link Builder} for method chaining
         */
        public Builder setLabel(Label label)
        {
            this.label = label;
            return this;
        }

        /**
         * Sets the icon that will be displayed on the button. The provided resource must be a
         * sprite, not a texture.
         *
         * @param sprite a {@link ResourceLocation} to a sprite image
         * @param width  the width of the sprite in pixels
         * @param height the height of the sprite in pixels
         * @return this {@link Builder} for method chaining
         */
        public Builder setIcon(ResourceLocation sprite, int width, int height)
        {
            this.icon = btn -> Icon.sprite(sprite, width, height);
            return this;
        }

        /**
         * Supplies an icon that will be displayed on the button. Unlike {@link #setIcon(ResourceLocation, int, int)},
         * the supplier is called every frame the button is drawn, which allows the sprite resource
         * to dynamically change. For example, this could be used to draw a different icon when the
         * button is on or off.
         *
         * @param sprite a {@link Supplier} that returns a {@link ResourceLocation} to a sprite image
         * @param width  the width of the sprite in pixels
         * @param height the height of the sprite in pixels
         * @return this {@link Builder} for method chaining
         */
        public Builder setIcon(Supplier<ResourceLocation> sprite, int width, int height)
        {
            this.icon = btn -> Icon.sprite(sprite, width, height);
            return this;
        }

        /**
         * Similar to {@link #setIcon(Supplier, int, int)} but with the ability to reference the
         * button when creating the {@link Supplier}. Please note that the sprite function is only
         * called once to create the {@link Supplier}, unlike the created supplier which is called
         * every frame the button is drawn.
         *
         * @param sprite a {@link Function} that provides context of the {@link FrameworkButton} to
         *               aid the creation of the {@link Supplier}, which returns a {@link ResourceLocation}
         *               to a sprite image
         * @param width  the width of the sprite in pixels
         * @param height the height of the sprite in pixels
         * @return this {@link Builder} for method chaining
         */
        public Builder setIcon(Function<FrameworkButton, Supplier<ResourceLocation>> sprite, int width, int height)
        {
            this.icon = btn -> Icon.sprite(sprite.apply(btn), width, height);
            return this;
        }

        /**
         * Sets the icon that will be displayed on the button but allows for custom implementations
         * of {@link Icon}. Use built-in functions {@link Icon#sprite(ResourceLocation, int, int)} and
         * {@link Icon#sprite(Supplier, int, int)} to create an icon for a sprite resources, otherwise
         * custom implements can be used to draw anything.
         *
         * @param icon an {@link Icon} to be used as the icon for this button
         * @return this {@link Builder} for method chaining
         */
        public Builder setIcon(Icon icon)
        {
            this.icon = btn -> icon;
            return this;
        }

        /**
         * Similar to {@link #setIcon(Icon)} but with the ability to reference the button when
         * creating the {@link Icon}. Please note that the provided function is only called once
         * upon the creation of the button.
         *
         * @param icon a {@link Function} that returns an {@link Icon}
         * @return this {@link Builder} for method chaining
         */
        public Builder setIcon(Function<FrameworkButton, Icon> icon)
        {
            this.icon = icon;
            return this;
        }

        /**
         * Sets the spacing between the icon and label of the button. The spacing if only applied
         * when the button has both an icon and label, not one or the other.
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
         * An alias method for {@link #setPrimaryAction(Action)}. Sets the action to run when the
         * button is left-clicked (the primary click).
         *
         * @param action a {@link Consumer} that accepts the {@link FrameworkButton}
         * @return this {@link Builder} for method chaining
         */
        public Builder setAction(Consumer<FrameworkButton> action)
        {
            this.actions().put(MouseInput.LEFT_CLICK, Action.create(action));
            return this;
        }

        /**
         * Sets the action to run when the button is left-clicked (the primary click).
         *
         * @param action a {@link Consumer} that accepts the {@link FrameworkButton}
         * @return this {@link Builder} for method chaining
         */
        public Builder setPrimaryAction(Consumer<FrameworkButton> action)
        {
            this.actions().put(MouseInput.LEFT_CLICK, Action.create(action));
            return this;
        }

        /**
         * Sets the action to run when the button is left-clicked (the primary click), however the
         * action is wrapped in an {@link Action} object. Use {@link Action#create(Consumer)} or
         * {@link Action#create(Consumer, Sound)} to create an instance.
         *
         * @param action an {@link Action} object
         * @return this {@link Builder} for method chaining
         */
        public Builder setPrimaryAction(Action<FrameworkButton> action)
        {
            this.actions().put(MouseInput.LEFT_CLICK, action);
            return this;
        }

        /**
         * Sets the action to run when the button is right-clicked (the secondary click).
         *
         * @param action a {@link Consumer} that accepts the {@link FrameworkButton}
         * @return this {@link Builder} for method chaining
         */
        public Builder setSecondaryAction(Consumer<FrameworkButton> action)
        {
            this.actions().put(MouseInput.RIGHT_CLICK, Action.create(action));
            return this;
        }

        /**
         * Sets the action to run when the button is right-clicked (the secondary click), however
         * the action is wrapped in an {@link Action} object. Use {@link Action#create(Consumer)} or
         * {@link Action#create(Consumer, Sound)} to create an instance.
         *
         * @param action an {@link Action} object
         * @return this {@link Builder} for method chaining
         */
        public Builder setSecondaryAction(Action<FrameworkButton> action)
        {
            this.actions().put(MouseInput.RIGHT_CLICK, action);
            return this;
        }

        /**
         * Sets the action to run when the button is middle-clicked (the tertiary click).
         *
         * @param action a {@link Consumer} that accepts the {@link FrameworkButton}
         * @return this {@link Builder} for method chaining
         */
        public Builder setTertiaryAction(Consumer<FrameworkButton> action)
        {
            this.actions().put(MouseInput.MIDDLE_CLICK, Action.create(action));
            return this;
        }

        /**
         * Sets the action to run when the button is middle-clicked (the tertiary click), however
         * the action is wrapped in an {@link Action} object. Use {@link Action#create(Consumer)} or
         * {@link Action#create(Consumer, Sound)} to create an instance.
         *
         * @param action an {@link Action} object
         * @return this {@link Builder} for method chaining
         */
        public Builder setTertiaryAction(Action<FrameworkButton> action)
        {
            this.actions().put(MouseInput.MIDDLE_CLICK, action);
            return this;
        }

        /**
         * Sets the action to run when the button is clicked using the given {@link MouseInput}.
         *
         * @param input a {@link MouseInput}
         * @param action
         * @return this {@link Builder} for method chaining
         */
        public Builder setAction(MouseInput input, Action<FrameworkButton> action)
        {
            this.actions().put(input, action);
            return this;
        }

        /**
         * Sets the texture of the button.
         *
         * @param texture a {@link WidgetSprites} containing the texture resources
         * @return this {@link Builder} for method chaining
         */
        public Builder setTexture(WidgetSprites texture)
        {
            this.texture = () -> texture;
            return this;
        }

        /**
         * Sets the texture of the button using a {@link Supplier}
         *
         * @param texture a {@link Supplier} that returns a {@link WidgetSprites} resource
         * @return this {@link Builder} for method chaining
         */
        public Builder setTexture(Supplier<WidgetSprites> texture)
        {
            this.texture = texture;
            return this;
        }

        /**
         * Removes the texture of the button, only drawing the icon and label.
         *
         * @return this {@link Builder} for method chaining
         */
        public Builder noTexture()
        {
            this.texture = null;
            return this;
        }

        /**
         * Sets a dependency on an arbitrary boolean. This will update the
         * {@link AbstractWidget#active} property of the button.
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
         * Sets the tooltip for the button.
         *
         * @param tooltip
         * @return this {@link Builder} for method chaining
         */
        public Builder setTooltip(Tooltip tooltip)
        {
            this.tooltip = btn -> tooltip;
            return this;
        }

        /**
         * Sets the tooltip for the button. Please note that {@link FrameworkButton} will call the
         * provided function everytime the tooltip cache is invalidated. The cache by default is only
         * invalidated when the button is clicked. It is very common in mods to show additional
         * information only when the shift key is pressed, and by adding
         * {@link TooltipOptions#REBUILD_TOOLTIP_ON_SHIFT} via {@link #setTooltipOptions(int)} will
         * additionally invalidate the cache when the shift key is pressed or released.
         *
         * @param tooltip a {@link Function} that returns a {@link Tooltip}
         * @return this {@link Builder} for method chaining
         */
        public Builder setTooltip(Function<FrameworkButton, Tooltip> tooltip)
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
        public Builder setTooltipDelay(int delay)
        {
            this.tooltipDelay = delay;
            return this;
        }

        /**
         * Sets the tooltip options for the button. Tooltip options gives additional control over
         * how tooltips behave. All possible tooltip options can be found at {@link TooltipOptions}.
         * To apply multiple options, use a bitwise-or operation: <code>option1 | option2 | option3</code>
         *
         * @param options an int of option flags
         * @return this {@link Builder} for method chaining
         */
        public Builder setTooltipOptions(int options)
        {
            this.tooltipOptions = options;
            return this;
        }

        /**
         * Sets the {@link ContentRenderer} used to draw the content of this button. This includes
         * the layout, texture, icon, and label. Content Renderers give complete freedom of how the
         * button draws itself.
         *
         * @param renderer the {@link ContentRenderer} for the button
         * @return this {@link Builder} for method chaining
         */
        public Builder setContentRenderer(@Nullable ContentRenderer<FrameworkButton> renderer)
        {
            this.contentRenderer = renderer;
            return this;
        }
    }

    public static class DefaultContentRenderer implements ContentRenderer<FrameworkButton>
    {
        public DefaultContentRenderer() {}

        @Override
        public void draw(FrameworkButton button, GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
        {
            WidgetSprites texture = button.getTexture();
            if(texture != null)
            {
                RenderSystem.enableBlend();
                RenderSystem.enableDepthTest();
                graphics.setColor(1, 1, 1, button.active ? 1.0F : 0.5F);
                graphics.blitSprite(texture.get(button.active, button.isHoveredOrFocused() && button.active), button.getX(), button.getY(), button.getWidth(), button.getHeight());
                graphics.setColor(1, 1, 1, 1);
                RenderSystem.disableBlend();
            }

            Label label = button.getLabel();
            int contentWidth = label.width();
            int contentHeight = contentWidth > 0 ? label.height() + 1 : 0;
            if(button.icon != null)
            {
                // Only add gap if the message is not empty
                if(contentWidth > 0)
                {
                    contentWidth += button.spacing;
                }
                contentWidth += button.icon.width();
                contentHeight = Math.max(contentHeight, button.icon.height());
            }
            int contentLeft = button.getX() + (button.getWidth() - contentWidth) / 2;
            int contentTop = button.getY() + (button.getHeight() - contentHeight) / 2;
            int textX = contentLeft + (button.icon != null ? button.spacing + button.icon.width() : 0);
            int textY = contentTop + (contentHeight - label.height()) / 2 + 1;
            int textColour = button.active ? 0xFFFFFFFF : 0xFF666666;
            boolean textShadow = button.active;
            label.draw(graphics, textX, textY, textColour, textShadow);

            if(button.icon != null)
            {
                int iconX = contentLeft;
                int iconY = contentTop + (contentHeight - button.icon.height()) / 2;
                RenderSystem.enableBlend();
                graphics.setColor(1, 1, 1, button.active ? 1.0F : 0.5F);
                button.icon.draw(graphics, iconX, iconY, partialTick);
                graphics.setColor(1, 1, 1, 1);
                RenderSystem.disableBlend();
            }
        }
    }

    public static class ToggleContentRenderer implements ContentRenderer<FrameworkButton>
    {
        private static final int TOGGLE_SIZE = 6;
        private static final WidgetSprites TOGGLE_SPRITES = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "widget/button/toggle_on"),
            ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "widget/button/toggle_off"),
            ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "widget/button/toggle_on")
        );

        private final Supplier<Boolean> state;

        public ToggleContentRenderer(Supplier<Boolean> state)
        {
            this.state = state;
        }

        @Override
        public void draw(FrameworkButton button, GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
        {
            WidgetSprites texture = button.getTexture();
            if(texture != null)
            {
                RenderSystem.enableBlend();
                RenderSystem.enableDepthTest();
                graphics.setColor(1, 1, 1, button.active ? 1.0F : 0.5F);
                graphics.blitSprite(texture.get(button.active, button.isHoveredOrFocused() && button.active), button.getX(), button.getY(), button.getWidth(), button.getHeight());
                graphics.setColor(1, 1, 1, 1);
                RenderSystem.disableBlend();
            }

            Label label = button.getLabel();
            int contentLeft = button.getX() + TOGGLE_SIZE;
            int textX = contentLeft + (button.icon != null ? button.spacing + button.icon.width() : 0);
            int textY = button.getY() + (button.getHeight() - label.height()) / 2 + 1;
            int textColour = button.active ? 0xFFFFFFFF : 0xFF666666;
            boolean textShadow = button.active;
            label.draw(graphics, textX, textY, textColour, textShadow);

            if(button.icon != null)
            {
                int iconX = contentLeft;
                int iconY = button.getY() + (button.getHeight() - button.icon.height()) / 2;
                RenderSystem.enableBlend();
                graphics.setColor(1, 1, 1, button.active ? 1.0F : 0.5F);
                button.icon.draw(graphics, iconX, iconY, partialTick);
                graphics.setColor(1, 1, 1, 1);
                RenderSystem.disableBlend();
            }

            int yOffset = (button.getHeight() - TOGGLE_SIZE) / 2;
            int stateIconY = button.getY() + yOffset;
            int stateIconX = button.getX() + button.getWidth() - TOGGLE_SIZE - yOffset;
            RenderSystem.enableBlend();
            graphics.setColor(1, 1, 1, button.active ? 1.0F : 0.5F);
            graphics.blitSprite(TOGGLE_SPRITES.get(this.state.get(), button.isHovered()), stateIconX, stateIconY, TOGGLE_SIZE, TOGGLE_SIZE);
            graphics.setColor(1, 1, 1, 1);
            RenderSystem.disableBlend();
        }
    }

    public interface ContentRenderer<T>
    {
        void draw(T widget, GuiGraphics graphics, int mouseX, int mouseY, float partialTick);
    }
}
