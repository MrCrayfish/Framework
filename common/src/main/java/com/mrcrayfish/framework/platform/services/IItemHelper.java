package com.mrcrayfish.framework.platform.services;

import net.minecraft.world.item.ItemStack;

/**
 * Author: MrCrayfish
 */
public interface IItemHelper
{
    boolean isDamageable(ItemStack stack);

    float getRepairRatio(ItemStack stack);
}
