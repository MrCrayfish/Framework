package com.mrcrayfish.framework.event;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Author: MrCrayfish
 */
public class ForgeEvents
{
    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event)
    {
        if(event.phase == TickEvent.Phase.START)
        {
            TickEvents.START_SERVER.post().handle(event.getServer());
        }
        else
        {
            TickEvents.END_SERVER.post().handle(event.getServer());
        }
    }

    @SubscribeEvent
    public void onStartTracking(PlayerEvent.StartTracking event)
    {
        PlayerEvents.START_TRACKING_ENTITY.post().handle(event.getTarget(), event.getEntity());
    }

    @SubscribeEvent
    public void onStartTracking(PlayerEvent.StopTracking event)
    {
        PlayerEvents.END_TRACKING_ENTITY.post().handle(event.getTarget(), event.getEntity());
    }

    @SubscribeEvent
    public void onEntityJoinLevel(EntityJoinLevelEvent event)
    {
        EntityEvents.JOIN_LEVEL.post().handle(event.getEntity(), event.getLevel(), event.loadedFromDisk());
    }

    @SubscribeEvent
    public void onEntityLeaveLevel(EntityLeaveLevelEvent event)
    {
        EntityEvents.LEAVE_LEVEL.post().handle(event.getEntity(), event.getLevel());
    }
}
