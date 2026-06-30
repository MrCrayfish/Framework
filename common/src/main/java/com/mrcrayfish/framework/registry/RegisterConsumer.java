package com.mrcrayfish.framework.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
@FunctionalInterface
public interface RegisterConsumer<T>
{
    void accept(ResourceKey<@NotNull Registry<@NotNull T>> registryKey, ResourceKey<@NotNull T> key, Supplier<T> valueSupplier);
}
