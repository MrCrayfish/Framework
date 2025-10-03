package com.mrcrayfish.framework.registry;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public interface RegistryProxy<T>
{
    boolean containsKey(ResourceLocation id);

    @Nullable
    T getValue(ResourceLocation id);

    Holder<T> getHolder(ResourceLocation id);

    Iterable<T> iterable();
}
