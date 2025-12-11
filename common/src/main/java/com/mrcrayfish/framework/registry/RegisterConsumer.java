package com.mrcrayfish.framework.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
@FunctionalInterface
public interface RegisterConsumer<T>
{
    void accept(ResourceKey<Registry<T>> registryKey, Identifier name, Supplier<T> valueSupplier);
}
