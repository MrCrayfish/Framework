package com.mrcrayfish.framework.api.client.screen.widget;

import com.google.common.annotations.Beta;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.mrcrayfish.framework.api.client.screen.widget.element.Icon;
import com.mrcrayfish.framework.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
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
    private static final Identifier BACKWARDS_SPRITE = Utils.rl("widget/stepper/backwards");
    private static final Identifier FORWARDS_SPRITE = Utils.rl("widget/stepper/forwards");

    /**
     * A {@link FrameworkStepper.Type} that produces an {@link IntBuilder}.
     *
     * <p>This is used to create a stepper capable of handling integer values. The builder returned
     * by {@code FrameworkStepper.builder(FrameworkStepper.INT)} allows configuration of bounds,
     * stepping distances, and the initial value.
     */
    public static final Type<IntBuilder> INT = new Type<>(FrameworkStepper.IntBuilder::new);

    /**
     * A {@link FrameworkStepper.Type} that produces an {@link LongBuilder}.
     *
     * <p>This is used to create a stepper capable of handling long values. The builder returned
     * by {@code FrameworkStepper.builder(FrameworkStepper.LONG)} allows configuration of bounds,
     * stepping distances, and the initial value.
     */
    public static final Type<LongBuilder> LONG = new Type<>(FrameworkStepper.LongBuilder::new);

    /**
     * A {@link FrameworkStepper.Type} that produces an {@link FloatBuilder}.
     *
     * <p>This is used to create a stepper capable of handling float values. The builder returned
     * by {@code FrameworkStepper.builder(FrameworkStepper.FLOAT)} allows configuration of bounds,
     * stepping distances, and the initial value.
     */
    public static final Type<FloatBuilder> FLOAT = new Type<>(FrameworkStepper.FloatBuilder::new);

    /**
     * A {@link FrameworkStepper.Type} that produces an {@link DoubleBuilder}.
     *
     * <p>This is used to create a stepper capable of handling double values. The builder returned
     * by {@code FrameworkStepper.builder(FrameworkStepper.DOUBLE)} allows configuration of bounds,
     * stepping distances, and the initial value.
     */
    public static final Type<DoubleBuilder> DOUBLE = new Type<>(FrameworkStepper.DoubleBuilder::new);

    private final Controller<T> controller;
    private final FrameworkEditBox valueEditBox;
    private final FrameworkButton backwardsButton;
    private final FrameworkButton forwardsButton;
    private final @Nullable Supplier<Boolean> activeSupplier;
    private final int spacing;
    private boolean errored;

    private FrameworkStepper(int x, int y, int width, int height, Controller<T> controller, WidgetSprites editBoxBackground, WidgetSprites buttonTexture, int spacing, Icon backwardsIcon, Icon forwardsIcon, int textColour, int erroredTextColour, @Nullable Supplier<Boolean> activeSupplier)
    {
        super(x, y, width, height, CommonComponents.EMPTY);
        int buttonSize = Math.max(10, height);
        this.spacing = spacing;
        this.controller = controller;
        this.activeSupplier = activeSupplier;
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
            .setTextFormatter((text, index) -> {
                return FormattedCharSequence.forward(text, this.errored ? errorStyle : normalStyle);
            })
            .setClearOnRightClick(false)
            .build();
        this.backwardsButton = FrameworkButton.builder()
            .setDependent(() -> this.isActive() && controller.canStep(StepDirection.BACKWARDS))
            .setAction(btn -> {
                controller.step(StepDirection.BACKWARDS, Minecraft.getInstance().hasShiftDown());
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
                controller.step(StepDirection.FORWARDS, Minecraft.getInstance().hasShiftDown());
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
        if(this.activeSupplier != null)
        {
            this.active = this.activeSupplier.get();
        }
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
        this.updateWidgets();
    }

    @Override
    public void setY(int y)
    {
        super.setY(y);
        this.updateWidgets();
    }

    @Override
    public void setWidth(int width)
    {
        super.setWidth(width);
        this.updateWidgets();
    }

    @Override
    public void setHeight(int height)
    {
        super.setHeight(height);
        this.updateWidgets();
    }

    @Override
    public void setSize(int width, int height)
    {
        super.setSize(width, height);
        this.updateWidgets();
    }

    private void updateWidgets()
    {
        int buttonSize = Math.max(10, this.getHeight());
        this.valueEditBox.setPosition(this.getX() + buttonSize + this.spacing, this.getY());
        this.valueEditBox.setSize(this.getWidth() - buttonSize * 2 - this.spacing * 2, buttonSize);
        this.backwardsButton.setPosition(this.getX(), this.getY());
        this.backwardsButton.setSize(buttonSize, buttonSize);
        this.forwardsButton.setPosition(this.getX() + this.getWidth() - buttonSize, this.getY());
        this.forwardsButton.setSize(buttonSize, buttonSize);
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

    @Override
    protected int contentHeight()
    {
        return this.getHeight();
    }

    @Override
    protected double scrollRate()
    {
        return 0;
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
    public static sealed abstract class Builder<B extends Builder<B, T>, T> permits NumberBuilder
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
        protected @Nullable Supplier<Boolean> activeSupplier;

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

        /**
         * Sets a dependency on an arbitrary boolean. This will update the
         * {@link AbstractWidget#active} property of the stepper.
         *
         * @param active a {@link Supplier} returning a {@link Boolean} representing the dependent state
         * @return this {@link B} for method chaining
         */
        public B setDependent(Supplier<Boolean> active)
        {
            this.activeSupplier = active;
            return this.self();
        }
    }

    /**
     * A base abstract class for building number-based steppers with configurable options.
     *
     * <p>This class provides methods to configure the bounds, step sizes, and initial values
     * for a number stepper. It is intended to be extended by specific number type implementations.
     *
     * @param <B> the type of the builder subclass
     * @param <V> the number type handled by the builder
     */
    private static non-sealed abstract class NumberBuilder<B extends NumberBuilder<B, V>, V extends Number> extends Builder<B, V>
    {
        protected V initialValue;
        protected V minValue;
        protected V maxValue;
        protected V step;
        protected V bigStep;

        /**
         * Sets the initial number value for the stepper
         *
         * @param initialValue the initial value
         * @return this {@link IntBuilder} for method chaining
         */
        public B setInitialValue(V initialValue)
        {
            this.initialValue = initialValue;
            return this.self();
        }

        /**
         * Sets the minimum number value of the stepper (aka the lower bound)
         *
         * @param minValue the minimum number value
         * @return this {@link IntBuilder} for method chaining
         */
        public B setMinValue(V minValue)
        {
            this.minValue = minValue;
            return this.self();
        }

        /**
         * Sets the maximum number value of the stepper (aka the upper bound)
         *
         * @param maxValue the maximum number value
         * @return this {@link IntBuilder} for method chaining
         */
        public B setMaxValue(V maxValue)
        {
            this.maxValue = maxValue;
            return this.self();
        }

        /**
         * Sets the step distance to apply when either the backward or forward button is pressed
         *
         * @param step the step distance as an number
         * @return this {@link IntBuilder} for method chaining
         */
        public B setStep(V step)
        {
            this.step = step;
            return this.self();
        }

        /**
         * Sets the big step distance to apply when either the backward or forward button is pressed.
         * The big step is applied when the user is holding the shift key down.
         *
         * @param bigStep the step distance as a number
         * @return this {@link IntBuilder} for method chaining
         */
        public B setBigStep(V bigStep)
        {
            this.bigStep = bigStep;
            return this.self();
        }
    }

    /**
     * Constructs a {@link FrameworkStepper} that handles integer values.

     * <p>To start using this builder, begin with the code {@code FrameworkStepper.builder(FrameworkStepper.INT).build()}
     */
    public static final class IntBuilder extends NumberBuilder<IntBuilder, Integer>
    {
        private IntBuilder()
        {
            this.initialValue = 0;
            this.minValue = Integer.MIN_VALUE;
            this.maxValue = Integer.MAX_VALUE;
            this.step = 1;
            this.bigStep = 10;
        }

        @Override
        public FrameworkStepper<Integer> build()
        {
            return new FrameworkStepper<>(this.x, this.y, this.width, this.height, new IntController(this.minValue, this.maxValue, this.step, this.bigStep, this.initialValue, this.callback), this.editBoxBackground, this.buttonTexture, this.spacing, this.backwardsIcon, this.forwardsIcon, this.textColour, this.erroredTextColour, this.activeSupplier);
        }
    }

    /**
     * Constructs a {@link FrameworkStepper} that handles long values.

     * <p>To start using this builder, begin with the code {@code FrameworkStepper.builder(FrameworkStepper.LONG).build()}
     */
    public static final class LongBuilder extends NumberBuilder<LongBuilder, Long>
    {
        private LongBuilder()
        {
            this.initialValue = 0L;
            this.minValue = Long.MIN_VALUE;
            this.maxValue = Long.MAX_VALUE;
            this.step = 1L;
            this.bigStep = 10L;
        }

        @Override
        public FrameworkStepper<Long> build()
        {
            return new FrameworkStepper<>(this.x, this.y, this.width, this.height, new LongController(this.minValue, this.maxValue, this.step, this.bigStep, this.initialValue, this.callback), this.editBoxBackground, this.buttonTexture, this.spacing, this.backwardsIcon, this.forwardsIcon, this.textColour, this.erroredTextColour, this.activeSupplier);
        }
    }

    /**
     * Constructs a {@link FrameworkStepper} that handles float values.
     *
     * <p>To start using this builder, begin with the code {@code FrameworkStepper.builder(FrameworkStepper.FLAT).build()}
     */
    public static final class FloatBuilder extends NumberBuilder<FloatBuilder, Float>
    {
        private static final DecimalFormat DEFAULT_FORMATTER;

        static
        {
            DEFAULT_FORMATTER = new DecimalFormat("0.#");
            DEFAULT_FORMATTER.setMinimumFractionDigits(1);
            DEFAULT_FORMATTER.setMaximumFractionDigits(3);
            DEFAULT_FORMATTER.setDecimalSeparatorAlwaysShown(true);
        }

        private DecimalFormat formatter = DEFAULT_FORMATTER;

        private FloatBuilder()
        {
            this.initialValue = 0.0F;
            this.minValue = -Float.MAX_VALUE;
            this.maxValue = Float.MAX_VALUE;
            this.step = 1.0F;
            this.bigStep = 10.0F;
        }

        public FloatBuilder setFormatter(DecimalFormat formatter)
        {
            this.formatter = formatter;
            return this.self();
        }

        @Override
        public FrameworkStepper<Float> build()
        {
            return new FrameworkStepper<>(this.x, this.y, this.width, this.height, new FloatController(this.minValue, this.maxValue, this.step, this.bigStep, this.initialValue, this.callback, this.formatter), this.editBoxBackground, this.buttonTexture, this.spacing, this.backwardsIcon, this.forwardsIcon, this.textColour, this.erroredTextColour, this.activeSupplier);
        }
    }

    /**
     * Constructs a {@link FrameworkStepper} that handles double values.
     *
     * <p>To start using this builder, begin with the code {@code FrameworkStepper.builder(FrameworkStepper.DOUBLE).build()}
     */
    public static final class DoubleBuilder extends NumberBuilder<DoubleBuilder, Double>
    {
        private static final DecimalFormat DEFAULT_FORMATTER;

        static
        {
            DEFAULT_FORMATTER = new DecimalFormat("0.#");
            DEFAULT_FORMATTER.setMinimumFractionDigits(1);
            DEFAULT_FORMATTER.setMaximumFractionDigits(3);
            DEFAULT_FORMATTER.setDecimalSeparatorAlwaysShown(true);
        }

        private DecimalFormat formatter = DEFAULT_FORMATTER;

        private DoubleBuilder()
        {
            this.initialValue = 0.0;
            this.minValue = -Double.MAX_VALUE;
            this.maxValue = Double.MAX_VALUE;
            this.step = 1.0;
            this.bigStep = 10.0;
        }

        public DoubleBuilder setFormatter(DecimalFormat formatter)
        {
            this.formatter = formatter;
            return this.self();
        }

        @Override
        public FrameworkStepper<Double> build()
        {
            return new FrameworkStepper<>(this.x, this.y, this.width, this.height, new DoubleController(this.minValue, this.maxValue, this.step, this.bigStep, this.initialValue, this.callback, this.formatter), this.editBoxBackground, this.buttonTexture, this.spacing, this.backwardsIcon, this.forwardsIcon, this.textColour, this.erroredTextColour, this.activeSupplier);
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

    private static abstract class NumberController<V extends Number> extends Controller<V>
    {
        protected final V minValue;
        protected final V maxValue;
        protected final V step;
        protected final V bigStep;
        protected final @Nullable Consumer<V> callback;
        protected final Function<String, V> parser;
        protected final Function<V, String> stringify;
        protected final BiFunction<V, V, Integer> compare;
        private final Clamper<V, V, V, V> clamper;
        protected V value;

        private NumberController(V minValue, V maxValue, V step, V bigStep, V initialValue, @Nullable Consumer<V> callback, Function<String, V> parser, Function<V, String> stringify, BiFunction<V, V, Integer> compare, Clamper<V, V, V, V> clamper)
        {
            this.minValue = minValue;
            this.maxValue = maxValue;
            this.step = step;
            this.bigStep = bigStep;
            this.value = initialValue;
            this.callback = callback;
            this.parser = parser;
            this.stringify = stringify;
            this.compare = compare;
            this.clamper = clamper;
        }

        @Override
        protected V get()
        {
            return this.value;
        }

        @Override
        protected void set(V value)
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

        @Nullable
        protected V parseText(String s)
        {
            return this.parser.apply(s);
        }

        protected String toText()
        {
            return this.stringify.apply(this.value);
        }

        protected boolean canStep(StepDirection direction)
        {
            return switch(direction) {
                case BACKWARDS -> this.compare.apply(this.value, this.minValue) > 0;
                case FORWARDS ->this.compare.apply(this.value, this.maxValue) < 0;
            };
        }

        @Override
        protected void step(StepDirection direction, boolean shift)
        {
            this.value = this.clamper.apply(this.applyStep(this.value, direction, shift), this.minValue, this.maxValue);
        }

        protected abstract V applyStep(V value, StepDirection direction, boolean shift);

        @FunctionalInterface
        protected interface Clamper<V, L, U, R>
        {
            R apply(V v, L l, U u);
        }
    }

    private static final class IntController extends NumberController<Integer>
    {
        private IntController(Integer minValue, Integer maxValue, Integer step, Integer bigStep, Integer initialValue, @Nullable Consumer<Integer> callback)
        {
            super(minValue, maxValue, step, bigStep, initialValue, callback, Ints::tryParse, value -> Integer.toString(value), Integer::compare, Mth::clamp);
        }

        @Override
        protected Integer applyStep(Integer value, StepDirection direction, boolean shift)
        {
            int step = shift ? this.bigStep : this.step;
            return direction == StepDirection.BACKWARDS ? value - step : value + step;
        }
    }

    private static final class LongController extends NumberController<Long>
    {
        private LongController(Long minValue, Long maxValue, Long step, Long bigStep, Long initialValue, @Nullable Consumer<Long> callback)
        {
            super(minValue, maxValue, step, bigStep, initialValue, callback, Longs::tryParse, value -> Long.toString(value), Long::compare, Mth::clamp);
        }

        @Override
        protected Long applyStep(Long value, StepDirection direction, boolean shift)
        {
            long step = shift ? this.bigStep : this.step;
            return direction == StepDirection.BACKWARDS ? value - step : value + step;
        }
    }

    private static final class FloatController extends NumberController<Float>
    {
        private final DecimalFormat formatter;

        private FloatController(Float minValue, Float maxValue, Float step, Float bigStep, Float initialValue, @Nullable Consumer<Float> callback, DecimalFormat formatter)
        {
            super(minValue, maxValue, step, bigStep, initialValue, callback, Floats::tryParse, value -> Float.toString(value), Double::compare, Mth::clamp);
            this.formatter = formatter;
        }

        @Override
        protected String toText()
        {
            return this.formatter.format(this.value);
        }

        @Override
        protected Float applyStep(Float value, StepDirection direction, boolean shift)
        {
            float step = shift ? this.bigStep : this.step;
            return direction == StepDirection.BACKWARDS ? value - step : value + step;
        }
    }

    private static final class DoubleController extends NumberController<Double>
    {
        private final DecimalFormat formatter;

        private DoubleController(Double minValue, Double maxValue, Double step, Double bigStep, Double initialValue, @Nullable Consumer<Double> callback, DecimalFormat formatter)
        {
            super(minValue, maxValue, step, bigStep, initialValue, callback, Doubles::tryParse, value -> Double.toString(value), Double::compare, Mth::clamp);
            this.formatter = formatter;
        }

        @Override
        protected String toText()
        {
            return this.formatter.format(this.value);
        }

        @Override
        protected Double applyStep(Double value, StepDirection direction, boolean shift)
        {
            double step = shift ? this.bigStep : this.step;
            return direction == StepDirection.BACKWARDS ? value - step : value + step;
        }
    }

    public record Type<B extends Builder<B, ?>>(Supplier<B> factory) {}
}
