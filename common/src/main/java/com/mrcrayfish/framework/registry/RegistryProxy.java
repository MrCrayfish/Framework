package com.mrcrayfish.framework.registry;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;

public interface RegistryProxy<T>
{
    boolean contains(ResourceLocation id);

    Holder<T> getHolder(ResourceLocation id);

    Iterable<T> iterable();
}
