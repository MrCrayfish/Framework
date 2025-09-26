package com.mrcrayfish.framework.api.registry;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public final class FrameworkRegistry<T> extends WrappedRegistry<T>
{
    private final boolean sync;

    private FrameworkRegistry(ResourceLocation id, boolean sync)
    {
        super(ResourceKey.createRegistryKey(id));
        this.sync = sync;
    }

    /**
     * @return True if this registry will sync to clients
     */
    public boolean shouldSync()
    {
        return this.sync;
    }

    /**
     * Creates a new FrameworkRegistry builder
     *
     * @param id  the id to be used for the new registry
     * @param <T> The type of the object the registry will hold
     * @return A {@link Builder} instance for constructing a FrameworkRegistry
     */
    public static <T> Builder<T> builder(ResourceLocation id)
    {
        return new Builder<>(id);
    }

    public static class Builder<T>
    {
        private final ResourceLocation id;
        private boolean sync = true;

        private Builder(ResourceLocation id)
        {
            this.id = id;
        }

        /**
         * Sets whether this registry should sync to clients. By default, this is set to true.
         *
         * @param sync true to sync, false to not sync
         * @return this builder instance
         */
        public Builder<T> sync(boolean sync)
        {
            this.sync = sync;
            return this;
        }

        /**
         * Builds the FrameworkRegistry using the options set in this builder. Keep in mind that
         * FrameworkRegistry is not immediately available to call its methods. See
         * {@link WrappedRegistry} for more info.
         *
         * @return a new FrameworkRegistry instance.
         */
        public FrameworkRegistry<T> build()
        {
            return new FrameworkRegistry<>(this.id, this.sync);
        }
    }
}
