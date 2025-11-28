package com.mrcrayfish.framework.api.client.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mrcrayfish.framework.Constants;
import com.mrcrayfish.framework.api.client.screen.TooltipOptions;
import com.mrcrayfish.framework.api.client.screen.widget.element.Icon;
import com.mrcrayfish.framework.api.client.screen.widget.element.Label;
import com.mrcrayfish.framework.api.client.screen.widget.input.Action;
import com.mrcrayfish.framework.api.client.screen.widget.input.MouseInput;
import com.mrcrayfish.framework.api.client.screen.widget.renderer.ContentRenderer;
import com.mrcrayfish.framework.api.util.LabelAndDescription;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.EnumMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class FrameworkButton extends AbstractButton
{
    public static final WidgetSprites DEFAULT_SPRITES = new WidgetSprites(
        ResourceLocation.withDefaultNamespace("widget/button"),
        ResourceLocation.withDefaultNamespace("widget/button_disabled"),
        ResourceLocation.withDefaultNamespace("widget/button_highlighted")
    );
    public static final int DEFAULT_TOOLTIP_DELAY = 350;
    public static final ContentRenderer<FrameworkButton> DEFAULT_CONTENT_RENDERER = new DefaultContentRenderer();
    public static final ContentRenderer<FrameworkButton> TOGGLE_CONTENT_RENDERER = new ToggleContentRenderer();

    private final Label label;
    private final @Nullable Icon icon;
    private final int spacing;
    private final @Nullable EnumMap<MouseInput, Action<FrameworkButton>> actions;
    private final @Nullable WidgetSprites texture;
    private final @Nullable Controller controller;
    private final @Nullable Supplier<Boolean> activeSupplier;
    private final @Nullable Function<FrameworkButton, Tooltip> tooltip;
    private final int tooltipOptions;
    private @Nullable Tooltip currentTooltip;
    private boolean shiftWasDown;
    private final @Nullable ContentRenderer<FrameworkButton> contentRenderer;

    private FrameworkButton(int x, int y, int width, int height, Label label, Function<FrameworkButton, Icon> icon, int spacing, @Nullable EnumMap<MouseInput, Action<FrameworkButton>> actions, @Nullable WidgetSprites texture, @Nullable Controller controller, @Nullable Supplier<Boolean> activeSupplier, @Nullable Function<FrameworkButton, Tooltip> tooltip, int tooltipDelay, int tooltipOptions, @Nullable ContentRenderer<FrameworkButton> contentRenderer)
    {
        super(x, y, width, height, CommonComponents.EMPTY);
        this.label = label;
        this.icon = icon.apply(this);
        this.spacing = spacing;
        this.actions = actions;
        this.texture = texture;
        this.controller = controller;
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
     * The underlying message supplier for this button. Since the message (aka the label) of the
     * button can be dynamic, using {@link #getMessage()} will return the Component that represents
     * the label based on dynamic supplier at the time of calling the method. This method returns
     * the underlying supplier so
     *
     * @return
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
        return this.texture;
    }

    /**
     * @return The pixel spacing used between the icon and message
     */
    public int getSpacing()
    {
        return this.spacing;
    }

    private void onAction(int button)
    {
        if(button == 0)
        {
            if(this.controller != null)
            {
                this.controller.run();
            }
        }
        Holder<SoundEvent> sound = SoundEvents.UI_BUTTON_CLICK;
        Action<FrameworkButton> action = this.actions != null ? this.actions.get(MouseInput.fromButton(button)) : null;
        if(action != null)
        {
            action.handler().accept(this);
            sound = action.sound();
        }
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sound, 1.0F));
        this.rebuildTooltip();
    }

    private void updateActiveState()
    {
        if(this.activeSupplier != null)
        {
            this.active = this.activeSupplier.get();
        }
    }

    private void rebuildTooltip()
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
        }
        return false;
    }

    @Override
    protected boolean isValidClickButton(int button)
    {
        return button == 0 || this.actions != null && this.actions.containsKey(button);
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static Builder state(Supplier<Boolean> getter, Consumer<Boolean> setter)
    {
        return new Builder(new StateController(getter, setter));
    }

    public static <T extends Enum<T> & LabelAndDescription> Builder values(Supplier<T> getter, Consumer<T> setter)
    {
        return new Builder(new EnumController<>(getter, setter))
            .setLabel(() -> getter.get().label())
            .setTooltip(btn -> Tooltip.create(getter.get().description()));
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
        private @Nullable WidgetSprites texture = DEFAULT_SPRITES;
        private @Nullable Controller controller;
        private @Nullable Supplier<Boolean> active;
        private @Nullable Function<FrameworkButton, Tooltip> tooltip;
        private int tooltipDelay = DEFAULT_TOOLTIP_DELAY;
        private int tooltipOptions;
        private @Nullable ContentRenderer<FrameworkButton> contentRenderer = FrameworkButton.DEFAULT_CONTENT_RENDERER;

        private Builder() {}

        private Builder(@Nullable Controller controller)
        {
            this.controller = controller;
        }

        public FrameworkButton build()
        {
            return new FrameworkButton(this.x, this.y, this.width, this.height, this.label, this.icon, this.spacing, this.actions, this.texture, this.controller, this.active, this.tooltip, this.tooltipDelay, this.tooltipOptions, this.contentRenderer);
        }

        private EnumMap<MouseInput, Action<FrameworkButton>> actions()
        {
            if(this.actions == null)
            {
                this.actions = new EnumMap<>(MouseInput.class);
            }
            return this.actions;
        }

        public Builder setX(int x)
        {
            this.x = x;
            return this;
        }

        public Builder setY(int y)
        {
            this.y = y;
            return this;
        }

        public Builder setPosition(int x, int y)
        {
            this.x = x;
            this.y = y;
            return this;
        }

        public Builder setWidth(int width)
        {
            this.width = width;
            return this;
        }

        public Builder setHeight(int height)
        {
            this.height = height;
            return this;
        }

        public Builder setSize(int width, int height)
        {
            this.width = width;
            this.height = height;
            return this;
        }

        public Builder setLabel(Component text)
        {
            this.label = Label.create(text);
            return this;
        }

        public Builder setLabel(Supplier<Component> supplier)
        {
            this.label = Label.create(supplier);
            return this;
        }

        public Builder setLabel(Label label)
        {
            this.label = label;
            return this;
        }

        public Builder setIcon(ResourceLocation sprite, int width, int height)
        {
            this.icon = btn -> Icon.sprite(sprite, width, height);
            return this;
        }

        public Builder setIcon(Supplier<ResourceLocation> sprite, int width, int height)
        {
            this.icon = btn -> Icon.sprite(sprite, width, height);
            return this;
        }

        public Builder setIcon(Function<FrameworkButton, Supplier<ResourceLocation>> sprite, int width, int height)
        {
            this.icon = btn -> Icon.sprite(sprite.apply(btn), width, height);
            return this;
        }

        public Builder setIcon(Icon icon)
        {
            this.icon = btn -> icon;
            return this;
        }

        public Builder setIcon(Function<FrameworkButton, Icon> icon)
        {
            this.icon = icon;
            return this;
        }

        public Builder setSpacing(int spacing)
        {
            this.spacing = spacing;
            return this;
        }

        public Builder setAction(Consumer<FrameworkButton> action)
        {
            this.actions().put(MouseInput.LEFT_CLICK, Action.create(action));
            return this;
        }

        public Builder setPrimaryAction(Consumer<FrameworkButton> action)
        {
            this.actions().put(MouseInput.LEFT_CLICK, Action.create(action));
            return this;
        }

        public Builder setPrimaryAction(Action<FrameworkButton> action)
        {
            this.actions().put(MouseInput.LEFT_CLICK, action);
            return this;
        }

        public Builder setSecondaryAction(Consumer<FrameworkButton> action)
        {
            this.actions().put(MouseInput.RIGHT_CLICK, Action.create(action));
            return this;
        }

        public Builder setSecondaryAction(Action<FrameworkButton> action)
        {
            this.actions().put(MouseInput.RIGHT_CLICK, action);
            return this;
        }

        public Builder setTertiaryAction(Consumer<FrameworkButton> action)
        {
            this.actions().put(MouseInput.MIDDLE_CLICK, Action.create(action));
            return this;
        }

        public Builder setTertiaryAction(Action<FrameworkButton> action)
        {
            this.actions().put(MouseInput.MIDDLE_CLICK, action);
            return this;
        }

        public Builder setAction(MouseInput input, Action<FrameworkButton> action)
        {
            this.actions().put(input, action);
            return this;
        }

        public Builder setTexture(WidgetSprites texture)
        {
            this.texture = texture;
            return this;
        }

        public Builder noTexture()
        {
            this.texture = null;
            return this;
        }

        public Builder setActive(Supplier<Boolean> active)
        {
            this.active = active;
            return this;
        }

        public Builder setTooltip(Function<FrameworkButton, Tooltip> tooltip)
        {
            this.tooltip = tooltip;
            return this;
        }

        public Builder setTooltipDelay(int delay)
        {
            this.tooltipDelay = delay;
            return this;
        }

        public Builder setTooltipOptions(int options)
        {
            this.tooltipOptions = options;
            return this;
        }

        public Builder setContentRenderer(@Nullable ContentRenderer<FrameworkButton> renderer)
        {
            this.contentRenderer = renderer;
            return this;
        }
    }

    private interface Controller
    {
        void run();
    }

    private record StateController(Supplier<Boolean> getter, Consumer<Boolean> setter) implements Controller
    {
        @Override
        public void run()
        {
            this.setter.accept(!this.getter.get());
        }
    }

    private record EnumController<T extends Enum<T> & LabelAndDescription>(Supplier<T> getter, Consumer<T> setter) implements Controller
    {
        @Override
        public void run()
        {
            T currentValue = this.getter.get();
            T[] values = currentValue.getDeclaringClass().getEnumConstants();
            T nextValue = values[(currentValue.ordinal() + 1) % values.length];
            this.setter.accept(nextValue);
        }
    }

    private static class DefaultContentRenderer implements ContentRenderer<FrameworkButton>
    {
        private DefaultContentRenderer() {}

        @Override
        public void draw(FrameworkButton button, GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
        {
            if(button.texture != null)
            {
                RenderSystem.enableBlend();
                RenderSystem.enableDepthTest();
                graphics.setColor(1, 1, 1, button.active ? 1.0F : 0.5F);
                graphics.blitSprite(button.texture.get(button.active, button.isHovered() && button.active), button.getX(), button.getY(), button.getWidth(), button.getHeight());
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

    private static class ToggleContentRenderer implements ContentRenderer<FrameworkButton>
    {
        private static final int TOGGLE_SIZE = 6;
        private static final WidgetSprites TOGGLE_SPRITES = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "button/toggle_on"),
            ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "button/toggle_off"),
            ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "button/toggle_on")
        );

        private ToggleContentRenderer() {}

        @Override
        public void draw(FrameworkButton button, GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
        {
            if(button.texture != null)
            {
                RenderSystem.enableBlend();
                RenderSystem.enableDepthTest();
                graphics.setColor(1, 1, 1, button.active ? 1.0F : 0.5F);
                graphics.blitSprite(button.texture.get(button.active, button.isHovered() && button.active), button.getX(), button.getY(), button.getWidth(), button.getHeight());
                graphics.setColor(1, 1, 1, 1);
                RenderSystem.disableBlend();
            }

            Label label = button.getLabel();
            int contentLeft = button.getX() + 6;
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
            boolean value = button.controller instanceof StateController state ? state.getter.get() : false;
            graphics.blitSprite(TOGGLE_SPRITES.get(value, button.isHovered()), stateIconX, stateIconY, TOGGLE_SIZE, TOGGLE_SIZE);
        }
    }
}
