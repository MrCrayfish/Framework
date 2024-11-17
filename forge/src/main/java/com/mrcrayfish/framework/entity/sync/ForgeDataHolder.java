package com.mrcrayfish.framework.entity.sync;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

/**
 * Author: MrCrayfish
 */
@AutoRegisterCapability
public class ForgeDataHolder extends DataHolder
{
    @Override
    public ForgeDataHolder setup(Entity entity)
    {
        return (ForgeDataHolder) super.setup(entity);
    }
}
