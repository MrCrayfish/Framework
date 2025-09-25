package com.mrcrayfish.framework.api.registry;

import com.mrcrayfish.framework.registry.RegistryProxy;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public final class FrameworkRegistry<T> implements Iterable<T>
{
    private final ResourceKey<Registry<T>> key;
    private final boolean save;
    private final boolean sync;
    private @Nullable RegistryProxy<T> proxy;

    private FrameworkRegistry(ResourceLocation id, boolean save, boolean sync)
    {
        this.key = ResourceKey.createRegistryKey(id);
        this.save = save;
        this.sync = sync;
    }

    public ResourceKey<Registry<T>> getKey()
    {
        return this.key;
    }

    public boolean shouldSave()
    {
        return this.save;
    }

    public boolean shouldSync()
    {
        return this.sync;
    }

    @NotNull
    @Override
    public Iterator<T> iterator()
    {
        return this.getProxy().iterable().iterator();
    }

    @ApiStatus.Internal
    RegistryProxy<T> getProxy()
    {
        if(this.proxy == null)
            throw new IllegalStateException("Registry not created yet");
        return this.proxy;
    }

    @ApiStatus.Internal
    @SuppressWarnings("unchecked")
    public void setProxy(RegistryProxy<?> registry)
    {
        this.proxy = (RegistryProxy<T>) registry;
    }

    /*@ApiStatus.Internal
    public static <T> FrameworkRegistry<T> wrapVanilla(Registry<T> registry)
    {
        FrameworkRegistry<T> r = new FrameworkRegistry<>(registry.key().location(), false, false);
        r.setProxy(VanillaRegistryProxy.wrap(registry));
        return r;
    }*/

    public static <T> Builder<T> builder(ResourceLocation id)
    {
        return new Builder<>(id);
    }

    public static class Builder<T>
    {
        private final ResourceLocation id;
        private boolean save = true;
        private boolean sync = true;

        private Builder(ResourceLocation id)
        {
            this.id = id;
        }

        public Builder<T> save(boolean save)
        {
            this.save = save;
            return this;
        }

        public Builder<T> sync(boolean sync)
        {
            this.sync = sync;
            return this;
        }

        public FrameworkRegistry<T> build()
        {
            return new FrameworkRegistry<>(this.id, this.save, this.sync);
        }
    }
}
