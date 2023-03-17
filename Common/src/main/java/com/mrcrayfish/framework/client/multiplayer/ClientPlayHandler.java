package com.mrcrayfish.framework.client.multiplayer;

import com.mrcrayfish.framework.entity.sync.DataEntry;
import com.mrcrayfish.framework.entity.sync.SyncedEntityData;
import com.mrcrayfish.framework.network.message.play.S2CUpdateEntityData;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public final class ClientPlayHandler
{
    public static void handleSyncEntityData(S2CUpdateEntityData message)
    {
        Level level = Minecraft.getInstance().level;
        if(level == null)
            return;

        Entity entity = level.getEntity(message.getEntityId());
        if(entity == null)
            return;

        List<DataEntry<?, ?>> entries = message.getEntries();
        entries.forEach(entry -> SyncedEntityData.instance().updateClientEntry(entity, entry));
    }
}
