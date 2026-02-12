package com.mrcrayfish.framework.api.client.screen.widget;

import com.google.common.annotations.Beta;
import com.google.common.primitives.Ints;
import com.mrcrayfish.framework.api.client.screen.widget.element.Icon;
import com.mrcrayfish.framework.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A widget that allows a user to edit a value by typing or by using forward and backward buttons.
 * The stepper delegates all value logic to the supplied {@link Controller} instance which ensures
 * correct bounds, stepping behaviour, and validation.
 *
 * <p>Note: Widget is still in a beta phase. Breaking changes may occur until beta status is removed.
 *
 * @param <T> the type of value managed by this stepper
 */
@Beta
public class FrameworkStepper<T> extends AbstractContainerWidget
{
    private static final ResourceLocation BACKWARDS_SPRITE = Utils.rl("widget/stepper/backwards");
    private static final ResourceLocation FORWARDS_SPRITE = Utils.rl("widget/stepper/forwards");

    /**
     * A {@link FrameworkStepper.Type} that produces an {@link IntBuilder}.
     *
     * <p>This is used to create a stepper capable of handling integer values. The builder returned
     * by {@code FrameworkStepper.builder(FrameworkStepper.INT)} allows configuration of bounds,
     * stepping distances, and the initial value.
     */
    public static final Type<IntBuilder> INT = new Type<>(FrameworkStepper.IntBuilder::new);

    private final Controller<T> controller;
    private final FrameworkEditBox valueEditBox;
    private final FrameworkButton backwardsButton;
    private final FrameworkButton forwardsButton;
    private final int spacing;
    private boolean errored;

    private FrameworkStepper(int x, int y, int width, int height, Controller<T> controller, WidgetSprites editBoxBackground, WidgetSprites buttonTexture, int spacing, Icon backwardsIcon, Icon forwardsIcon, int textColour, int erroredTextColour)
    {
        super(x, y, width, height, CommonComponents.EMPTY);
        int buttonSize = Math.max(10, height);
        this.spacing = spacing;
        this.controller = controller;
        Style normalStyle = Style.EMPTY.withColor(textColour);
        Style errorStyle = Style.EMPTY.withColor(erroredTextColour);
        this.valueEditBox = FrameworkEditBox.builder()
            .setDependent(() -> this.isActive() && controller.allowsTextEditing())
            .setInitialText(controller.toText())
            .setSize(width - buttonSize * 2 - spacing * 2, buttonSize)
            .setPosition(x + buttonSize + spacing, y)
            .setBackground(editBoxBackground)
            .setCallback(s -> {
                this.errored = false;
                T value = controller.parseText(s);
                if(value != null) {
                    controller.set(value);
                } else {
                    this.errored = true;
                }
            })
            .setStyleFormatter((text, index) -> {
                return FormattedCharSequence.forward(text, this.errored ? errorStyle : normalStyle);
            })
            .setClearOnRightClick(false)
            .build();
        this.backwardsButton = FrameworkButton.builder()
            .setDependent(() -> this.isActive() && controller.canStep(StepDirection.BACKWARDS))
            .setAction(btn -> {
                controller.step(StepDirection.BACKWARDS, Screen.hasShiftDown());
                this.valueEditBox.getEditBox().setValue(controller.toText());
            })
            .setSize(buttonSize, buttonSize)
            .setPosition(x, y)
            .setTexture(buttonTexture)
            .setIcon(backwardsIcon)
            .build();
        this.forwardsButton = FrameworkButton.builder()
            .setDependent(() -> this.isActive() && controller.canStep(StepDirection.FORWARDS))
            .setAction(btn -> {
                controller.step(StepDirection.FORWARDS, Screen.hasShiftDown());
                this.valueEditBox.getEditBox().setValue(controller.toText());
            })
            .setSize(buttonSize, buttonSize)
            .setPosition(x + width - buttonSize, y)
            .setTexture(buttonTexture)
            .setIcon(forwardsIcon)
            .build();

        // Triggers an update to check if errored
        this.valueEditBox.getEditBox().setValue(controller.toText());
    }

    /**
     * Retrieves the current value of this stepper
     *
     * @return the current value of type {@code T}
     */
    public T getValue()
    {
        return this.controller.get();
    }

    /**
     * Indicates whether the stepper is currently in an error state, which can happen if the user
     * enters an invalid value into the edit box.
     *
     * @return {@code true} if the stepper has encountered an error; otherwise {@code false}
     */
    public boolean isErrored()
    {
        return this.errored;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        this.children().forEach(listener -> {
            if(listener instanceof AbstractWidget widget) {
                widget.render(graphics, mouseX, mouseY, partialTick);
            }
        });
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {}

    @Override
    public void setX(int x)
    {
        super.setX(x);
        int buttonSize = Math.max(10, this.getHeight());
        this.valueEditBox.setX(x + buttonSize + this.spacing);
        this.backwardsButton.setX(x);
        this.forwardsButton.setX(x + this.getWidth() - buttonSize);
    }

    @Override
    public void setY(int y)
    {
        super.setY(y);
        this.valueEditBox.setY(y);
        this.backwardsButton.setY(y);
        this.forwardsButton.setY(y);
    }

    @Override
    public List<? extends GuiEventListener> children()
    {
        return List.of(this.backwardsButton, this.valueEditBox, this.forwardsButton);
    }

    @Override
    public void setFocused(boolean focused)
    {
        super.setFocused(focused);

        // Hack to get focusing to work with the container widget
        if(!focused)
        {
            this.children().forEach(listener -> listener.setFocused(false));
        }
        else if(this.getFocused() != null)
        {
            this.getFocused().setFocused(true);
        }
    }

    private enum StepDirection
    {
        BACKWARDS, FORWARDS;
    }

    /**
     * Creates a new {@link FrameworkStepper} instance using the supplied builder type.
     *
     * <p>Available types for the stepper builder are: {@link #INT}
     *
     * @param <B>  the specific {@link Builder} subtype used to construct the stepper
     * @param type a {@link FrameworkStepper.Type} record holding a factory that supplies a new builder instance
     * @return a new {@code B} builder instance
     */
    public static <B extends Builder<B, ?>> B builder(FrameworkStepper.Type<B> type)
    {
        return type.factory.get();
    }

    /**
     * A builder used to configure and create {@link FrameworkStepper} instances.
     *
     * <p>The builder provides common configuration options shared by all stepper types, such as
     * position, size, spacing between controls, button textures, and icons. Concrete builders,
     * like {@link IntBuilder}, extend this class to expose additional domain-specific
     * parameters (e.g., bounds, initial value or stepping distance) before constructing the
     * final {@link FrameworkStepper} via {@link #build()}.
     *
     * <p>Each setter returns {@code this} cast to the concrete builder type {@code B},
     * enabling method chaining. The generic parameter {@code T} represents the type of
     * value managed by the stepper.
     *
     * @param <B> the concrete builder subtype extending this class
     * @param <T> the value type handled by the resulting {@link FrameworkStepper}
     */
    public static sealed abstract class Builder<B extends Builder<B, T>, T> permits IntBuilder
    {
        protected int x;
        protected int y;
        protected int width;
        protected int height;
        protected int spacing;
        protected WidgetSprites editBoxBackground = FrameworkEditBox.DEFAULT_SPRITES;
        protected WidgetSprites buttonTexture = FrameworkButton.DEFAULT_SPRITES;
        protected Icon backwardsIcon = Icon.sprite(BACKWARDS_SPRITE, 6, 8);
        protected Icon forwardsIcon = Icon.sprite(FORWARDS_SPRITE, 6, 8);
        protected @Nullable Consumer<T> callback;
        protected int textColour = 0xFFFFFFFF;
        protected int erroredTextColour = 0xFFFF5555;

        private Builder() {}

        protected abstract FrameworkStepper<T> build();

        @SuppressWarnings("unchecked")
        protected B self()
        {
            return (B) this;
        }

        /**
         * Sets the x position of this stepper.
         *
         * @param x the x position of the stepper in pixel units
         * @return this {@link B} for method chaining
         */
        public B setX(int x)
        {
            this.x = x;
            return this.self();
        }

        /**
         * Sets the y position of this stepper.
         *
         * @param y the y position of the stepper in pixel units
         * @return this {@link B} for method chaining
         */
        public B setY(int y)
        {
            this.y = y;
            return this.self();
        }

        /**
         * Sets the x and y position of the stepper. This method is simply for convenience to set the
         * x and y position in a single call.
         *
         * @param x the x position of the stepper in pixel units
         * @param y the y position of the stepper in pixel units
         * @return this {@link B} for method chaining
         */
        public B setPosition(int x, int y)
        {
            this.x = x;
            this.y = y;
            return this.self();
        }

        /**
         * Sets the width of the stepper.
         *
         * @param width the width of the stepper in pixel units
         * @return this {@link B} for method chaining
         */
        public B setWidth(int width)
        {
            this.width = width;
            return this.self();
        }

        /**
         * Sets the height of the stepper. The default height is 20 to match vanilla buttons.
         *
         * @param height the height of the stepper in pixel units
         * @return this {@link B} for method chaining
         */
        public B setHeight(int height)
        {
            this.height = height;
            return this.self();
        }

        /**
         * Sets the width and height (the size) of the stepper. The default height is 20 to match
         * vanilla buttons. This method is simply for convenience to set the width and height in
         * a single call.
         *
         * @param width  the width of the stepper in pixel units
         * @param height the height of the stepper in pixel units
         * @return this {@link B} for method chaining
         */
        public B setSize(int width, int height)
        {
            this.width = width;
            this.height = height;
            return this.self();
        }

        /**
         * Sets the spacing to apply between the buttons and edit box of the stepper
         *
         * @param spacing the spacing in pixel units
         * @return this {@link B} for method chaining
         */
        public B setSpacing(int spacing)
        {
            this.spacing = spacing;
            return this.self();
        }

        /**
         * Sets the background texture of the edit box.
         *
         * @param background a {@link WidgetSprites} containing the texture resources
         * @return this {@link B} for method chaining
         */
        public B setEditBoxBackground(@Nullable WidgetSprites background)
        {
            this.editBoxBackground = background;
            return this.self();
        }

        /**
         * Sets the textures to use for the buttons of the stepper
         *
         * @param texture a {@link WidgetSprites} instance containing the texture definitions
         * @return this {@link B} for method chaining
         */
        public B setButtonTexture(WidgetSprites texture)
        {
            this.buttonTexture = texture;
            return this.self();
        }

        /**
         * Sets the icon for the backwards button of the stepper
         *
         * @param icon an {@link Icon} instance
         * @return this {@link B} for method chaining
         */
        public B setBackwardsIcon(Icon icon)
        {
            this.backwardsIcon = icon;
            return this.self();
        }

        /**
         * Sets the icon for the forwards button of the stepper
         *
         * @param icon an {@link Icon} instance
         * @return this {@link B} for method chaining
         */
        public B setForwardsIcon(Icon icon)
        {
            this.forwardsIcon = icon;
            return this.self();
        }

        /**
         * Sets the callback when the stepper changes value. This will call the provided consumer
         * with the new value of type {@link T}.
         *
         * @param callback a consumer that accepts type {@link T}
         * @return this {@link B} for method chaining
         */
        public B setCallback(@Nullable Consumer<T> callback)
        {
            this.callback = callback;
            return this.self();
        }

        /**
         * Sets the colour of the text in the edit box of the stepper
         *
         * @param colour the colour in decimal format
         * @return this {@link B} for method chaining
         */
        public B setTextColour(int colour)
        {
            this.textColour = colour;
            return this.self();
        }

        /**
         * Sets the errored colour of the text in the edit box of the stepper
         *
         * @param colour the colour in decimal format
         * @return this {@link B} for method chaining
         */
        public B setErroredTextColour(int colour)
        {
            this.erroredTextColour = colour;
            return this.self();
        }
    }

    /**
     * Constructs a {@link FrameworkStepper} that handles integer values.
     *
     * <p>The builder allows configuration of the stepper's bounds, initial value, stepping distance,
     * and visual elements inherited from {@link Builder}. The resulting {@link FrameworkStepper}
     * uses an internal {@link IntController} to enforce limits and perform increments or decrements.
     *
     * <p>To start using this builder, begin with the code {@code FrameworkStepper.builder(FrameworkStepper.INT).build()}
     */
    public static final class IntBuilder extends Builder<IntBuilder, Integer>
    {
        private int initialValue;
        private int minValue = Integer.MIN_VALUE;
        private int maxValue = Integer.MAX_VALUE;
        private int step = 1;
        private int bigStep = 10;

        private IntBuilder() {}

        @Override
        public FrameworkStepper<Integer> build()
        {
            return new FrameworkStepper<>(this.x, this.y, this.width, this.height, new IntController(this.minValue, this.maxValue, this.step, this.bigStep, this.initialValue, this.callback), this.editBoxBackground, this.buttonTexture, this.spacing, this.backwardsIcon, this.forwardsIcon, this.textColour, this.erroredTextColour);
        }

        /**
         * Sets the initial integer value for the stepper
         *
         * @param initialValue the initial value
         * @return this {@link IntBuilder} for method chaining
         */
        public IntBuilder setInitialValue(int initialValue)
        {
            this.initialValue = initialValue;
            return this.self();
        }

        /**
         * Sets the minimum integer value of the stepper (aka the lower bound)
         *
         * @param minValue the minimum integer value
         * @return this {@link IntBuilder} for method chaining
         */
        public IntBuilder setMinValue(int minValue)
        {
            this.minValue = minValue;
            return this.self();
        }

        /**
         * Sets the maximum integer value of the stepper (aka the upper bound)
         *
         * @param maxValue the maximum integer value
         * @return this {@link IntBuilder} for method chaining
         */
        public IntBuilder setMaxValue(int maxValue)
        {
            this.maxValue = maxValue;
            return this.self();
        }

        /**
         * Sets the step distance to apply when either the backward or forward button is pressed
         *
         * @param step the step distance as an integer
         * @return this {@link IntBuilder} for method chaining
         */
        public IntBuilder setStep(int step)
        {
            this.step = step;
            return this.self();
        }

        /**
         * Sets the big step distance to apply when either the backward or forward button is pressed.
         * The big step is applied when the user is holding the shift key down.
         *
         * @param bigStep the step distance as an integer
         * @return this {@link IntBuilder} for method chaining
         */
        public IntBuilder setBigStep(int bigStep)
        {
            this.bigStep = bigStep;
            return this.self();
        }
    }

    private static abstract class Controller<T>
    {
        protected abstract T get();

        protected abstract void set(T value);

        protected abstract boolean allowsTextEditing();

        @Nullable
        protected abstract T parseText(String s);

        protected abstract String toText();

        protected abstract boolean canStep(StepDirection direction);

        protected abstract void step(StepDirection direction, boolean shift);
    }

    private static final class IntController extends Controller<Integer>
    {
        private final int minValue;
        private final int maxValue;
        private final int step;
        private final int bigStep;
        private final @Nullable Consumer<Integer> callback;
        private int value;

        private IntController(int minValue, int maxValue, int step, int bigStep, int initialValue, @Nullable Consumer<Integer> callback)
        {
            this.minValue = minValue;
            this.maxValue = maxValue;
            this.step = step;
            this.bigStep = bigStep;
            this.value = initialValue;
            this.callback = callback;
        }

        @Override
        protected Integer get()
        {
            return this.value;
        }

        @Override
        protected void set(Integer value)
        {
            this.value = value;
            if(this.callback != null)
            {
                this.callback.accept(this.value);
            }
        }

        protected boolean allowsTextEditing()
        {
            return true;
        }

        @Override
        protected Integer parseText(String s)
        {
            return Ints.tryParse(s);
        }

        @Override
        protected String toText()
        {
            return Integer.toString(this.value);
        }

        @Override
        protected boolean canStep(StepDirection direction)
        {
            return switch(direction) {
                case BACKWARDS -> this.value > this.minValue;
                case FORWARDS -> this.value < this.maxValue;
            };
        }

        @Override
        protected void step(StepDirection direction, boolean shift)
        {
            int step = shift ? this.bigStep : this.step;
            switch(direction) {
                case BACKWARDS -> this.value = Mth.clamp(this.value - step, this.minValue, this.maxValue);
                case FORWARDS -> this.value = Mth.clamp(this.value + step, this.minValue, this.maxValue);
            }
        }
    }

    public record Type<B extends Builder<B, ?>>(Supplier<B> factory) {}
}
