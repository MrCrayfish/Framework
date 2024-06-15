package com.mrcrayfish.framework.util;

import com.mrcrayfish.framework.Constants;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class Utils
{
    public static ResourceLocation rl(String name)
    {
        return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name);
    }

    public static <T> Optional<T> or(T a, T b)
    {
        if(a != null) return Optional.of(a);
        if(b != null) return Optional.of(b);
        return Optional.empty();
    }
}
