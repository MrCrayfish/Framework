package com.mrcrayfish.framework.api.client.screen;

import com.google.common.annotations.Beta;
import com.mrcrayfish.framework.api.client.screen.widget.FrameworkButton;
import com.mrcrayfish.framework.api.util.LabelAndDescription;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

public final class Buttons
{
    private Buttons() {}

    public static FrameworkButton.Builder createOnOffOption(Component label, Supplier<Boolean> getter, Consumer<Boolean> setter)
    {
        return createOnOffOption(label, getter, setter, value -> {});
    }

    public static FrameworkButton.Builder createOnOffOption(Component label, Supplier<Boolean> getter, Consumer<Boolean> setter, Consumer<Boolean> callback)
    {
        return FrameworkButton.builder().setAction(btn -> {
            boolean newValue = !getter.get();
            setter.accept(newValue);
            callback.accept(newValue);
        }).setLabel(() -> CommonComponents.optionStatus(label, getter.get()));
    }

    public static FrameworkButton.Builder toggle(Supplier<Boolean> getter, Consumer<Boolean> setter)
    {
        return toggle(getter, setter, newValue -> {});
    }

    public static FrameworkButton.Builder toggle(Supplier<Boolean> getter, Consumer<Boolean> setter, Consumer<Boolean> onChanged)
    {
        return FrameworkButton.builder().setAction(btn -> {
            boolean newValue = !getter.get();
            setter.accept(newValue);
            onChanged.accept(newValue);
        }).setContentRenderer(new FrameworkButton.ToggleContentRenderer(getter));
    }

    public static <T extends Enum<T> & LabelAndDescription> FrameworkButton.Builder values(Supplier<T> getter, Consumer<T> setter)
    {
        return values(getter, setter, t -> {});
    }

    public static <T extends Enum<T> & LabelAndDescription> FrameworkButton.Builder values(Supplier<T> getter, Consumer<T> setter, Consumer<T> onChanged)
    {
        return FrameworkButton.builder()
            .setLabel(() -> getter.get().label())
            .setTooltip(btn -> Tooltip.create(getter.get().description()))
            .setAction(btn -> {
                T currentValue = getter.get();
                T[] values = currentValue.getDeclaringClass().getEnumConstants();
                T nextValue = values[(currentValue.ordinal() + 1) % values.length];
                setter.accept(nextValue);
                onChanged.accept(nextValue);
            });
    }
}
