package com.mrcrayfish.framework.client.multiplayer;

import com.mrcrayfish.framework.common.data.SyncedPlayerData;
import com.mrcrayfish.framework.network.message.play.S2CUpdatePlayerData;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public class ClientPlayHandler
{
    public static void handleSyncPlayerData(S2CUpdatePlayerData message)
    {
        Level level = Minecraft.getInstance().level;
        if(level == null)
            return;

        Entity entity = level.getEntity(message.getEntityId());
        if(!(entity instanceof Player player))
            return;

        List<SyncedPlayerData.DataEntry<?>> entries = message.getEntries();
        entries.forEach(entry -> SyncedPlayerData.instance().updateClientEntry(player, entry));
    }
}
