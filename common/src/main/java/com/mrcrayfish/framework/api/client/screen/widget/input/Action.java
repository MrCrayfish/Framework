package com.mrcrayfish.framework.api.client.screen.widget.input;

import com.mrcrayfish.framework.api.client.screen.widget.element.Sound;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Represents an action that can be triggered, with an associated handler and optional sound effect.
 *
 * @param <T> the type of the argument consumed by the action handler
 */
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

    /**
     * @return The handler to execute when this action is triggered
     */
    public Consumer<T> handler()
    {
        return this.handler;
    }

    /**
     * @return The sound to play when this action is triggered, or null if no sound should be played
     */
    @Nullable
    public Sound sound()
    {
        return this.sound;
    }

    /**
     * Creates a new {@link Action} with the specified {@link Consumer} handler. This action will
     * play the default UI click sound, {@link SoundEvents#UI_BUTTON_CLICK}.
     *
     * @param action a {@link Consumer} to execute when this action is triggered
     * @return the created {@link Action} instance
     */
    public static <T> Action<T> create(Consumer<T> action)
    {
        return new Action<>(action, DEFAULT_SOUND);
    }

    /**
     * Creates a new {@link Action} with the specified {@link Consumer} handler and custom sound.
     *
     * @param action a {@link Consumer} to execute when this action is triggered
     * @param sound  a {@link Sound} to play when this action is triggered, or null if no sound should be played
     * @return the created {@link Action} instance
     */
    public static <T> Action<T> create(Consumer<T> action, @Nullable Sound sound)
    {
        return new Action<>(action, sound);
    }
}
