package com.mrcrayfish.framework.api.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
@FunctionalInterface
public interface IRegisterFunction
{
    <T> void call(Registry<T> registry, ResourceLocation name, Supplier<T> valueSupplier);
}
