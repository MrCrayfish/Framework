package com.mrcrayfish.framework.registry;

import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

public interface RegistryProxy<T>
{
    boolean containsKey(Identifier id);

    @Nullable
    T getValue(Identifier id);

    Holder<T> getHolder(Identifier id);

    Iterable<T> iterable();
}
