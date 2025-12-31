package com.mrcrayfish.framework.api.client.screen;

import com.mrcrayfish.framework.api.client.screen.widget.FrameworkButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Buttons
{
    private Buttons() {}

    public static FrameworkButton.Builder createOnOff(Supplier<Boolean> getter, Consumer<Boolean> setter)
    {
        return createOnOff(getter, setter, newValue -> {});
    }

    public static FrameworkButton.Builder createOnOff(Supplier<Boolean> getter, Consumer<Boolean> setter, Consumer<Boolean> onChanged)
    {
        return FrameworkButton.builder().setAction(btn -> {
            boolean newValue = !getter.get();
            setter.accept(newValue);
            onChanged.accept(newValue);
        }).setLabel(() -> CommonComponents.optionStatus(getter.get()));
    }

    public static FrameworkButton.Builder createOnOff(Component label, Supplier<Boolean> getter, Consumer<Boolean> setter)
    {
        return createOnOff(label, getter, setter, value -> {});
    }

    public static FrameworkButton.Builder createOnOff(Component label, Supplier<Boolean> getter, Consumer<Boolean> setter, Consumer<Boolean> onChanged)
    {
        return FrameworkButton.builder().setAction(btn -> {
            boolean newValue = !getter.get();
            setter.accept(newValue);
            onChanged.accept(newValue);
        }).setLabel(() -> CommonComponents.optionStatus(label, getter.get()));
    }

    public static FrameworkButton.Builder createToggle(Supplier<Boolean> getter, Consumer<Boolean> setter)
    {
        return createToggle(CommonComponents.EMPTY, getter, setter, newValue -> {});
    }

    public static FrameworkButton.Builder createToggle(Supplier<Boolean> getter, Consumer<Boolean> setter, Consumer<Boolean> onChanged)
    {
        return createToggle(CommonComponents.EMPTY, getter, setter, onChanged);
    }

    public static FrameworkButton.Builder createToggle(Component label, Supplier<Boolean> getter, Consumer<Boolean> setter)
    {
        return createToggle(label, getter, setter, newValue -> {});
    }

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

    public static <T extends Enum<T>> FrameworkButton.Builder createValues(Function<T, Component> label, Supplier<T> getter, Consumer<T> setter)
    {
        return createValues(label, t -> null, getter, setter, newValue -> {});
    }

    public static <T extends Enum<T>> FrameworkButton.Builder createValues(Function<T, Component> label, Supplier<T> getter, Consumer<T> setter, Consumer<T> onChanged)
    {
        return createValues(label, t -> null, getter, setter, onChanged);
    }

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
