package com.mrcrayfish.framework.api.client.screen.widget;

import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Utility class for creating pre-configured {@link FrameworkButton.Builder} instances
 * for common button behaviours, such as toggling boolean values or cycling through enum values.
 */
public final class Buttons
{
    private Buttons() {}

    /**
     * Creates a {@link FrameworkButton.Builder} configured for an on/off option. The label is
     * generated automatically based on the current value, being either OFF or ON.
     *
     * @param getter a supplier that returns the current boolean value
     * @param setter a consumer that accepts and sets the new boolean value when the button is clicked
     * @return a configured {@link FrameworkButton.Builder} for an on/off option
     */
    public static FrameworkButton.Builder createOnOff(Supplier<Boolean> getter, Consumer<Boolean> setter)
    {
        return createOnOff(getter, setter, newValue -> {});
    }

    /**
     * Creates a {@link FrameworkButton.Builder} configured for an on/off option, with a callback
     * for when the value changes. The label is generated automatically based on the current value,
     * being either OFF or ON.
     *
     * @param getter    a supplier that returns the current boolean value
     * @param setter    a consumer that accepts and sets the new boolean value when the button is clicked
     * @param onChanged a consumer that is called with the new value after it changes
     * @return a configured {@link FrameworkButton.Builder} for an on/off option
     */
    public static FrameworkButton.Builder createOnOff(Supplier<Boolean> getter, Consumer<Boolean> setter, Consumer<Boolean> onChanged)
    {
        return FrameworkButton.builder().setAction(btn -> {
            boolean newValue = !getter.get();
            setter.accept(newValue);
            onChanged.accept(newValue);
        }).setLabel(() -> CommonComponents.optionStatus(getter.get()));
    }

    /**
     * Creates a {@link FrameworkButton.Builder} configured for an on/off option that is prefixed
     * with a custom label followed by either OFF or ON.
     *
     * @param label  the {@link Component} to prefix as the button label
     * @param getter a supplier that returns the current boolean value
     * @param setter a consumer that accepts and sets the new boolean value when the button is clicked
     * @return a configured {@link FrameworkButton.Builder} for an on/off option
     */
    public static FrameworkButton.Builder createOnOff(Component label, Supplier<Boolean> getter, Consumer<Boolean> setter)
    {
        return createOnOff(label, getter, setter, value -> {});
    }

    /**
     * Creates a {@link FrameworkButton.Builder} configured for an on/off option that is prefixed
     * with a custom label followed by either OFF or ON, and a callback for when the value changes.
     *
     * @param label     the {@link Component} to display as the button label
     * @param getter    a supplier that returns the current boolean value
     * @param setter    a consumer that accepts and sets the new boolean value when the button is clicked
     * @param onChanged a consumer that is called with the new value after it changes
     * @return a configured {@link FrameworkButton.Builder} for an on/off option
     */
    public static FrameworkButton.Builder createOnOff(Component label, Supplier<Boolean> getter, Consumer<Boolean> setter, Consumer<Boolean> onChanged)
    {
        return FrameworkButton.builder().setAction(btn -> {
            boolean newValue = !getter.get();
            setter.accept(newValue);
            onChanged.accept(newValue);
        }).setLabel(() -> CommonComponents.optionStatus(label, getter.get()));
    }

    /**
     * Creates a {@link FrameworkButton.Builder} configured for toggling a boolean value.
     * The label is empty by default.
     *
     * @param getter a supplier that returns the current boolean value
     * @param setter a consumer that accepts and sets the new boolean value when the button is clicked
     * @return a configured {@link FrameworkButton.Builder} for a toggle button
     */
    public static FrameworkButton.Builder createToggle(Supplier<Boolean> getter, Consumer<Boolean> setter)
    {
        return createToggle(CommonComponents.EMPTY, getter, setter, newValue -> {});
    }

    /**
     * Creates a {@link FrameworkButton.Builder} configured for toggling a boolean value, with a
     * callback for when the value changes. The label is empty by default.
     *
     * @param getter    a supplier that returns the current boolean value
     * @param setter    a consumer that accepts and sets the new boolean value when the button is clicked
     * @param onChanged a consumer that is called with the new value after it changes
     * @return a configured {@link FrameworkButton.Builder} for a toggle button
     */
    public static FrameworkButton.Builder createToggle(Supplier<Boolean> getter, Consumer<Boolean> setter, Consumer<Boolean> onChanged)
    {
        return createToggle(CommonComponents.EMPTY, getter, setter, onChanged);
    }

    /**
     * Creates a {@link FrameworkButton.Builder} configured for toggling a boolean value with a
     * custom label.
     *
     * @param label  the {@link Component} to display as the button label
     * @param getter a supplier that returns the current boolean value
     * @param setter a consumer that accepts and sets the new boolean value when the button is clicked
     * @return a configured {@link FrameworkButton.Builder} for a toggle button
     */
    public static FrameworkButton.Builder createToggle(Component label, Supplier<Boolean> getter, Consumer<Boolean> setter)
    {
        return createToggle(label, getter, setter, newValue -> {});
    }

    /**
     * Creates a {@link FrameworkButton.Builder} configured for toggling a boolean value with a
     * custom label and a callback for when the value changes.
     *
     * @param label     the {@link Component} to display as the button label
     * @param getter    a supplier that returns the current boolean value
     * @param setter    a consumer that accepts and sets the new boolean value when the button is clicked
     * @param onChanged a consumer that is called with the new value after it changes
     * @return a configured {@link FrameworkButton.Builder} for a toggle button
     */
    public static FrameworkButton.Builder createToggle(Component label, Supplier<Boolean> getter, Consumer<Boolean> setter, Consumer<Boolean> onChanged)
    {
        return FrameworkButton.builder()
            .setContentRenderer(new FrameworkButton.ToggleContentRenderer(getter))
            .setLabel(label)
            .setAction(btn -> {
                boolean newValue = !getter.get();
                setter.accept(newValue);
                onChanged.accept(newValue);
            });
    }

    /**
     * Creates a {@link FrameworkButton.Builder} configured to cycle through the values of an enum.
     * The label is determined by the provided function. Clicking the button cycles to the next enum
     * value and updates it via the setter.
     *
     * @param <T>    the type of the enum
     * @param label  a function that takes the current enum value and returns a {@link Component} for the button label
     * @param getter a supplier that returns the current enum value
     * @param setter a consumer that accepts and sets the new enum value when the button is clicked
     * @return a configured {@link FrameworkButton.Builder} for an enum value button
     */
    public static <T extends Enum<T>> FrameworkButton.Builder createValues(Function<T, Component> label, Supplier<T> getter, Consumer<T> setter)
    {
        return createValues(label, t -> null, getter, setter, newValue -> {});
    }

    /**
     * Creates a {@link FrameworkButton.Builder} configured to cycle through the values of an enum,
     * with a callback that is called when the value changes. The label is determined by the provided
     * function.
     *
     * @param <T>      the type of the enum
     * @param label    a function that takes the current enum value and returns a {@link Component} for the button label
     * @param getter   a supplier that returns the current enum value
     * @param setter   a consumer that accepts and sets the new enum value when the button is clicked
     * @param onChanged a consumer that is called with the new enum value after it changes
     * @return a configured {@link FrameworkButton.Builder} for an enum value button
     */
    public static <T extends Enum<T>> FrameworkButton.Builder createValues(Function<T, Component> label, Supplier<T> getter, Consumer<T> setter, Consumer<T> onChanged)
    {
        return createValues(label, t -> null, getter, setter, onChanged);
    }

    /**
     * Creates a {@link FrameworkButton.Builder} configured to cycle through the values of an enum,
     * with a dynamic label and optional tooltip. Clicking the button cycles to the next enum value,
     * updates it via the setter, and calls the onChanged callback.
     *
     * @param <T>      the type of the enum
     * @param label    a function that takes the current enum value and returns a {@link Component} for the button label
     * @param tooltip  a function that takes the current enum value and returns a {@link Component} for the tooltip, or null for no tooltip.
     * @param getter   a supplier that returns the current enum value
     * @param setter   a consumer that accepts and sets the new enum value when the button is clicked
     * @param onChanged a consumer that is called with the new enum value after it changes
     * @return a configured {@link FrameworkButton.Builder} for an enum value button
     */
    public static <T extends Enum<T>> FrameworkButton.Builder createValues(Function<T, Component> label, Function<T, @Nullable Component> tooltip, Supplier<T> getter, Consumer<T> setter, Consumer<T> onChanged)
    {
        return FrameworkButton.builder()
            .setLabel(() -> label.apply(getter.get()))
            .setTooltip(btn -> {
                Component text = tooltip.apply(getter.get());
                return text != null ? Tooltip.create(text) : null;
            })
            .setAction(btn -> {
                T currentValue = getter.get();
                T[] values = currentValue.getDeclaringClass().getEnumConstants();
                T nextValue = values[(currentValue.ordinal() + 1) % values.length];
                setter.accept(nextValue);
                onChanged.accept(nextValue);
            });
    }
}
