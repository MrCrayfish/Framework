package com.mrcrayfish.framework.util;

import com.mrcrayfish.framework.Constants;
import net.minecraft.resources.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public class Utils
{
    public static ResourceLocation rl(String name)
    {
        return new ResourceLocation(Constants.MOD_ID, name);
    }
}
