package com.mrcrayfish.framework.util;

import com.mrcrayfish.framework.Constants;
import net.minecraft.resources.Identifier;

import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public class Utils
{
    public static Identifier rl(String name)
    {
        return Identifier.fromNamespaceAndPath(Constants.MOD_ID, name);
    }

    public static <T> Optional<T> or(T a, T b)
    {
        if(a != null) return Optional.of(a);
        if(b != null) return Optional.of(b);
        return Optional.empty();
    }
}
