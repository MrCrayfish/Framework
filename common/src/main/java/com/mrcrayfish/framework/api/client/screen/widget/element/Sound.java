package com.mrcrayfish.framework.api.client.screen.widget.element;

import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents an abstract sound construct that allows for dynamic or fixed configurations
 * of sound events, volume, and pitch.
 */
public abstract class Sound
{
    /**
     * The default volume level for UI sounds.
     */
    public static final float DEFAULT_UI_VOLUME = 0.25F;

    /**
     * The default pitch value for UI sounds.
     */
    public static final float DEFAULT_UI_PITCH = 1.0F;

    /**
     * @return a {@link Holder} containing the {@link SoundEvent}
     */
    public abstract SoundEvent value();

    /**
     * @return the volume of the sound as a float value, potentially varying depending on implementation.
     */
    public abstract float volume();

    /**
     * @return the pitch value as a float, potentially varying depending on implementation.
     */
    public abstract float pitch();

    /**
     * Creates a new {@link Sound} instance using the specified {@link Holder} of {@link SoundEvent}.
     * The default values for ui volume and pitch will be used.
     *
     * @param holder a {@link Holder} containing the {@link SoundEvent}
     * @return a new {@link Sound} using the specified holder and default volume and pitch values
     */
    public static Sound create(Holder<SoundEvent> holder)
    {
        return new Impl(holder::value, rand -> DEFAULT_UI_VOLUME, rand -> DEFAULT_UI_PITCH);
    }

    /**
     * Creates a new {@link Sound} instance with the specified {@link SoundEvent} holder, volume,
     * and pitch.
     *
     * @param holder a {@link Holder} containing the {@link SoundEvent}
     * @param volume the volume of the sound, specified as a float
     * @param pitch  the pitch of the sound, specified as a float
     * @return a new {@code Sound} using the provided holder, volume, and pitch
     */
    public static Sound create(Holder<SoundEvent> holder, float volume, float pitch)
    {
        return new Impl(holder::value, rand -> volume, rand -> pitch);
    }

    /**
     * Creates a new {@link Sound} instance with the specified {@link SoundEvent} holder, and
     * functions to compute volume and pitch dynamically.
     *
     * @param holder         a {@link Holder} containing the {@link SoundEvent}
     * @param volumeFunction a function that computes the volume for the sound with a {@link RandomSource}
     * @param pitchFunction  a function that computes the pitch for the sound with a {@link RandomSource}
     * @return a new {@code Sound} using the provided holder, volume function, and pitch function
     */
    public static Sound create(Holder<SoundEvent> holder, Function<RandomSource, Float> volumeFunction, Function<RandomSource, Float> pitchFunction)
    {
        return new Impl(holder::value, volumeFunction, pitchFunction);
    }

    /**
     * Creates a new {@link Sound} instance using the specified {@link SoundEvent}. The default
     * values for volume and pitch are set as static values of 1.0.
     *
     * @param event a {@link SoundEvent}
     * @return a new {@link Sound} instance initialized with the given {@link SoundEvent} and default volume and pitch values
     */
    public static Sound create(SoundEvent event)
    {
        return new Impl(() -> event, rand -> DEFAULT_UI_VOLUME, rand -> DEFAULT_UI_PITCH);
    }

    /**
     * Creates a new {@link Sound} instance using the specified {@link SoundEvent}, volume, and
     * pitch values.
     *
     * @param event  a {@link SoundEvent}
     * @param volume the volume of the sound, specified as a float
     * @param pitch  the pitch of the sound, specified as a float
     * @return a new {@link Sound} instance with the provided event, volume, and pitch
     */
    public static Sound create(SoundEvent event, float volume, float pitch)
    {
        return new Impl(() -> event, rand -> volume, rand -> pitch);
    }

    /**
     * Creates a new {@link Sound} instance using the specified {@link SoundEvent} and functions to
     * compute volume and pitch dynamically based on a {@link RandomSource}.
     *
     * @param event          a {@link SoundEvent} used to define the sound
     * @param volumeFunction a function that computes the volume for the sound based on a {@link RandomSource}
     * @param pitchFunction  a function that computes the pitch for the sound based on a {@link RandomSource}
     * @return a new {@link Sound} instance configured with the provided event, volume function, and pitch function
     */
    public static Sound create(SoundEvent event, Function<RandomSource, Float> volumeFunction, Function<RandomSource, Float> pitchFunction)
    {
        return new Impl(() -> event, volumeFunction, pitchFunction);
    }

    /**
     * Creates a new {@link Sound} instance using the specified {@link SoundEvent} supplier.
     * The default values for volume and pitch are set to 1.0.
     *
     * @param sound a supplier that returns a {@link SoundEvent}
     * @return a new {@link Sound} instance with the provided sound supplier and default volume and pitch
     */
    public static Sound create(Supplier<SoundEvent> sound)
    {
        return new Impl(sound, rand -> DEFAULT_UI_VOLUME, rand -> DEFAULT_UI_PITCH);
    }

    /**
     * Creates a new {@link Sound} instance using the specified {@link SoundEvent}, volume, and
     * pitch values.
     *
     * @param sound  a supplier that returns a {@link SoundEvent}
     * @param volume the volume of the sound, specified as a float
     * @param pitch  the pitch of the sound, specified as a float
     * @return a new {@link Sound} instance with the provided event, volume, and pitch
     */
    public static Sound create(Supplier<SoundEvent> sound, float volume, float pitch)
    {
        return new Impl(sound, rand -> volume, rand -> pitch);
    }

    /**
     * Creates a new {@link Sound} instance using the specified {@link SoundEvent} supplier
     * and functions to dynamically compute volume and pitch.
     *
     * @param sound          a supplier that returns a {@link SoundEvent}
     * @param volumeFunction a function that computes the volume for the sound with a {@link RandomSource}
     * @param pitchFunction  a function that computes the pitch for the sound with a {@link RandomSource}
     * @return a new {@link Sound} instance configured with the provided sound supplier, volume function, and pitch function
     */
    public static Sound create(Supplier<SoundEvent> sound, Function<RandomSource, Float> volumeFunction, Function<RandomSource, Float> pitchFunction)
    {
        return new Impl(sound, volumeFunction, pitchFunction);
    }

    private static class Impl extends Sound
    {
        private static final RandomSource RANDOM = RandomSource.create();

        private final Supplier<SoundEvent> soundSupplier;
        private final Function<RandomSource, Float> volumeFunction;
        private final Function<RandomSource, Float> pitchFunction;

        private Impl(Supplier<SoundEvent> soundSupplier, Function<RandomSource, Float> volumeFunction, Function<RandomSource, Float> pitchFunction)
        {
            this.soundSupplier = soundSupplier;
            this.volumeFunction = volumeFunction;
            this.pitchFunction = pitchFunction;
        }

        @Override
        public SoundEvent value()
        {
            return this.soundSupplier.get();
        }

        @Override
        public float volume()
        {
            return this.volumeFunction.apply(RANDOM);
        }

        @Override
        public float pitch()
        {
            return this.pitchFunction.apply(RANDOM);
        }
    }
}
