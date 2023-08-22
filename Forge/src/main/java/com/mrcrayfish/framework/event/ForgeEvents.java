package com.mrcrayfish.framework.event;

import com.mrcrayfish.framework.api.event.EntityEvents;
import com.mrcrayfish.framework.api.event.PlayerEvents;
import com.mrcrayfish.framework.api.event.ServerEvents;
import com.mrcrayfish.framework.api.event.TickEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
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

    @SubscribeEvent
    public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event)
    {
        PlayerEvents.CHANGE_DIMENSION.post().handle(event.getEntity(), event.getFrom(), event.getTo());
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.Clone event)
    {
        PlayerEvents.COPY.post().handle(event.getOriginal(), event.getEntity(), !event.isWasDeath());
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event)
    {
        PlayerEvents.RESPAWN.post().handle(event.getEntity(), event.isEndConquered());
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        PlayerEvents.LOGGED_IN.post().handle(event.getEntity());
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedOutEvent event)
    {
        PlayerEvents.LOGGED_OUT.post().handle(event.getEntity());
    }

    @SubscribeEvent
    public void onPickupItem(EntityItemPickupEvent event)
    {
        if(PlayerEvents.PICKUP_ITEM.post().handle(event.getEntity(), event.getItem()))
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onCraftItem(PlayerEvent.ItemCraftedEvent event)
    {
        PlayerEvents.CRAFT_ITEM.post().handle(event.getEntity(), event.getCrafting(), event.getInventory());
    }

    @SubscribeEvent
    public void onLivingEntityDeath(LivingDeathEvent event)
    {
        if(event.getEntity() instanceof Player player)
        {
            if(PlayerEvents.DEATH.post().handle(player, event.getSource()))
            {
                event.setCanceled(true);
            }
        }

        if(EntityEvents.LIVING_ENTITY_DEATH.post().handle(event.getEntity(), event.getSource()))
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPlayerPickupExp(PlayerXpEvent.PickupXp event)
    {
        if(PlayerEvents.PICKUP_EXPERIENCE.post().handle(event.getEntity(), event.getOrb()))
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onLivingTick(LivingEvent.LivingTickEvent event)
    {
        TickEvents.START_LIVING_ENTITY.post().handle(event.getEntity());
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if(event.phase == TickEvent.Phase.START)
        {
            TickEvents.START_PLAYER.post().handle(event.player);
        }
        else
        {
            TickEvents.END_PLAYER.post().handle(event.player);
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerAboutToStartEvent event)
    {
        ServerEvents.STARTING.post().handle(event.getServer());
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event)
    {
        ServerEvents.STARTED.post().handle(event.getServer());
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event)
    {
        ServerEvents.STOPPING.post().handle(event.getServer());
    }

    @SubscribeEvent
    public void onServerStopped(ServerStoppedEvent event)
    {
        ServerEvents.STOPPED.post().handle(event.getServer());
    }
}
