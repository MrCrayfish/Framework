package com.mrcrayfish.framework.platform;

import com.mrcrayfish.framework.platform.services.IItemHelper;
import net.minecraft.world.item.ItemStack;

/**
 * Author: MrCrayfish
 */
public class ForgeItemHelper implements IItemHelper
{
    @Override
    public boolean isDamageable(ItemStack stack)
    {
        return stack.getItem().isDamageable(stack);
    }

    @Override
    public float getRepairRatio(ItemStack stack)
    {
        return stack.getXpRepairRatio();
    }
}
