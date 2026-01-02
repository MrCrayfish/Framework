package com.mrcrayfish.framework.api.client.screen.widget.input;

import com.google.common.annotations.Beta;
import com.mrcrayfish.framework.api.client.screen.widget.element.Sound;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

@Beta
public final class Action<T>
{
    private static final Sound DEFAULT_SOUND = Sound.create(SoundEvents.UI_BUTTON_CLICK);

    private final Consumer<T> handler;
    private final @Nullable Sound sound;

    private Action(Consumer<T> handler, @Nullable Sound sound)
    {
        this.handler = handler;
        this.sound = sound;
    }

    public Consumer<T> handler()
    {
        return this.handler;
    }

    @Nullable
    public Sound sound()
    {
        return this.sound;
    }

    public static <T> Action<T> create(Consumer<T> action)
    {
        return new Action<>(action, DEFAULT_SOUND);
    }

    public static <T> Action<T> create(Consumer<T> action, @Nullable Sound sound)
    {
        return new Action<>(action, sound);
    }
}
