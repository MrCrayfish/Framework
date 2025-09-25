package com.mrcrayfish.framework.api.registry;

import com.mrcrayfish.framework.registry.RegistryProxy;
import com.mrcrayfish.framework.registry.VanillaRegistryProxy;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public class WrappedRegistry<T> implements Iterable<T>
{
    private final ResourceKey<Registry<T>> key;
    private @Nullable RegistryProxy<T> proxy;

    public WrappedRegistry(ResourceKey<Registry<T>> key)
    {
        this.key = key;
    }

    public ResourceKey<Registry<T>> getKey()
    {
        return this.key;
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

    @ApiStatus.Internal
    public static <T> WrappedRegistry<T> wrapVanilla(Registry<T> registry)
    {
        ResourceKey<Registry<T>> key = ResourceKey.createRegistryKey(registry.key().location());
        WrappedRegistry<T> wrapped = new WrappedRegistry<>(key);
        wrapped.proxy = VanillaRegistryProxy.wrap(registry);
        return wrapped;
    }
}
