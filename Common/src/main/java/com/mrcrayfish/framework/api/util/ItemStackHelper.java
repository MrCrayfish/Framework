package com.mrcrayfish.framework.api.util;

import com.mrcrayfish.framework.platform.Services;
import net.minecraft.world.item.ItemStack;

/**
 * Author: MrCrayfish
 */
public class ItemStackHelper
{
    public static boolean isDamageable(ItemStack stack)
    {
        return Services.ITEM.isDamageable(stack);
    }

    public static float getRepairRatio(ItemStack stack)
    {
        return Services.ITEM.getRepairRatio(stack);
    }
}
