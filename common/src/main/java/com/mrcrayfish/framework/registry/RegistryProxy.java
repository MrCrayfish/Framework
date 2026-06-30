package com.mrcrayfish.framework.registry;

import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RegistryProxy<T>
{
    boolean containsKey(Identifier id);

    @Nullable
    T getValue(Identifier id);

    Holder<@NotNull T> getHolder(Identifier id);

    Holder<@NotNull T> getHolder(ResourceKey<@NotNull T> id);

    Iterable<T> iterable();
}
