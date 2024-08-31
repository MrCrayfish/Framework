package com.mrcrayfish.framework.platform;

import com.mrcrayfish.framework.entity.sync.DataHolder;
import com.mrcrayfish.framework.entity.sync.ForgeSyncedEntityDataHandler;
import com.mrcrayfish.framework.platform.services.IEntityHelper;
import net.minecraft.world.entity.Entity;

import org.jetbrains.annotations.Nullable;

/**
 * Author: MrCrayfish
 */
public class ForgeEntityHelper implements IEntityHelper
{
    @Override
    @Nullable
    public DataHolder getDataHolder(Entity entity, boolean old)
    {
        if(old) entity.reviveCaps();
        DataHolder holder = entity.getCapability(ForgeSyncedEntityDataHandler.CAPABILITY, null).resolve().orElse(null);
        if(old) entity.invalidateCaps();
        return holder;
    }
}
