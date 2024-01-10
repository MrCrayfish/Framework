package com.mrcrayfish.framework.platform;

import com.mrcrayfish.framework.FrameworkNeoForge;
import com.mrcrayfish.framework.entity.sync.DataHolder;
import com.mrcrayfish.framework.platform.services.IEntityHelper;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class NeoForgeEntityHelper implements IEntityHelper
{
    @Override
    @Nullable
    public DataHolder getDataHolder(Entity entity, boolean old)
    {
        return entity.getData(FrameworkNeoForge.DATA_HOLDER);
    }
}
