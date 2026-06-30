package com.mrcrayfish.framework.registry;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;

public record VanillaRegistryProxy<T>(Registry<@NotNull T> registry) implements RegistryProxy<T>
{
    @Override
    public boolean containsKey(Identifier id)
    {
        return this.registry.containsKey(id);
    }

    @Override
    public T getValue(Identifier id)
    {
        return this.registry.getValue(id);
    }

    @Override
    public Holder<@NotNull T> getHolder(Identifier id)
    {
        return this.registry.get(id).orElseThrow();
    }

    @Override
    public Holder<@NotNull T> getHolder(ResourceKey<@NotNull T> key)
    {
        return this.registry.get(key).orElseThrow();
    }

    @Override
    public Iterable<T> iterable()
    {
        return this.registry;
    }

    public static <T> VanillaRegistryProxy<T> wrap(Registry<@NotNull T> registry)
    {
        return new VanillaRegistryProxy<>(registry);
    }
}
