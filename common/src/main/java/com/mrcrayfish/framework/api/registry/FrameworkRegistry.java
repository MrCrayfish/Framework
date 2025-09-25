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

    public boolean shouldSync()
    {
        return this.sync;
    }

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

        public Builder<T> sync(boolean sync)
        {
            this.sync = sync;
            return this;
        }

        public FrameworkRegistry<T> build()
        {
            return new FrameworkRegistry<>(this.id, this.sync);
        }
    }
}
