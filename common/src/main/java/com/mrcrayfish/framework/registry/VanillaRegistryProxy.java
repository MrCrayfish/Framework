package com.mrcrayfish.framework.registry;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public Holder<T> getHolder(ResourceLocation id)
    {
        return this.registry.getHolder(id).orElseThrow();
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
