package com.mrcrayfish.framework.api.config.validate;

import com.google.common.base.Preconditions;
import net.minecraft.network.chat.Component;

/**
 * A simple validator that to test if a number is within a range (inclusive).
 * <p>
 * Author: MrCrayfish
 */
public record NumberRange<T extends Number & Comparable<T>>(T minValue, T maxValue) implements Validator<T>
{
    public NumberRange
    {
        Preconditions.checkArgument(minValue.compareTo(maxValue) <= 0, "Min value must be less than or equal to the max value");
    }

    @Override
    public boolean test(T value)
    {
        return value.compareTo(this.minValue) >= 0 && value.compareTo(this.maxValue) <= 0;
    }

    @Override
    public Component getHint()
    {
        return Component.translatable("configured.validator.range_hint", this.minValue, this.maxValue);
    }
}
