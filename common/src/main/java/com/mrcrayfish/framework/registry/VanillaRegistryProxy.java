package com.mrcrayfish.framework.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public final class VanillaRegistryProxy<T> implements RegistryProxy<T>
{
    private final Registry<T> registry;

    private VanillaRegistryProxy(Registry<T> registry)
    {
        this.registry = registry;
    }

    @Override
    public boolean containsKey(ResourceLocation id)
    {
        return this.registry.containsKey(id);
    }

    @Override
    public T getValue(ResourceLocation id)
    {
        return this.registry.get(id);
    }

    @Override
    public Iterable<T> iterable()
    {
        return this.registry;
    }

    public static <T> VanillaRegistryProxy<T> wrap(Registry<T> registry)
    {
        return new VanillaRegistryProxy<>(registry);
    }
}
