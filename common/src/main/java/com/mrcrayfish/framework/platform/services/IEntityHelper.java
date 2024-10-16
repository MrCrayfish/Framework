package com.mrcrayfish.framework.platform.services;

import com.mrcrayfish.framework.entity.sync.DataHolder;
import net.minecraft.world.entity.Entity;

import org.jetbrains.annotations.Nullable;

/**
 * Author: MrCrayfish
 */
public interface IEntityHelper
{
    @Nullable
    DataHolder getDataHolder(Entity entity, boolean old);
}
