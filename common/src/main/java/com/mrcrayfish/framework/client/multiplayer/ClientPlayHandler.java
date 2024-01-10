package com.mrcrayfish.framework.client.multiplayer;

import com.mrcrayfish.framework.Constants;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.config.FrameworkConfigManager;
import com.mrcrayfish.framework.entity.sync.DataEntry;
import com.mrcrayfish.framework.entity.sync.SyncedEntityData;
import com.mrcrayfish.framework.network.message.play.S2CSyncConfigData;
import com.mrcrayfish.framework.network.message.play.S2CUpdateEntityData;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
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

        Entity entity = level.getEntity(message.entityId());
        if(entity == null)
            return;

        List<DataEntry<?, ?>> entries = message.entries();
        entries.forEach(entry -> SyncedEntityData.instance().updateClientEntry(entity, entry));
    }

    public static void handleSyncConfigData(MessageContext context, S2CSyncConfigData message)
    {
        // Avoid updating config if packet was sent to self
        if(Minecraft.getInstance().isLocalServer())
            return;

        Constants.LOG.debug("Received framework config sync from server");

        if(!FrameworkConfigManager.getInstance().processSyncData(message))
        {
            context.disconnect(Component.translatable("framework.multiplayer.disconnect.process_config"));
        }
    }
}
