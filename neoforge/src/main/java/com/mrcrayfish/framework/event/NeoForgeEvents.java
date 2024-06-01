package com.mrcrayfish.framework.event;

import com.mrcrayfish.framework.api.event.EntityEvents;
import com.mrcrayfish.framework.api.event.PlayerEvents;
import com.mrcrayfish.framework.api.event.ServerEvents;
import com.mrcrayfish.framework.api.event.TickEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/**
 * Author: MrCrayfish
 */
public class NeoForgeEvents
{
    @SubscribeEvent
    public void onServerTickPre(ServerTickEvent.Pre event)
    {
        TickEvents.START_SERVER.post().handle(event.getServer());
    }

    @SubscribeEvent
    public void onServerTickPost(ServerTickEvent.Post event)
    {
        TickEvents.END_SERVER.post().handle(event.getServer());
    }

    @SubscribeEvent
    public void onLevelTickPre(LevelTickEvent.Pre event)
    {
        TickEvents.START_LEVEL.post().handle(event.getLevel());
    }

    @SubscribeEvent
    public void onLevelTickPost(LevelTickEvent.Post event)
    {
        TickEvents.END_LEVEL.post().handle(event.getLevel());
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
    public void onPickupItem(ItemEntityPickupEvent.Pre event)
    {
        if(PlayerEvents.PICKUP_ITEM.post().handle(event.getPlayer(), event.getItemEntity()))
        {
            event.setCanPickup(TriState.FALSE);
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
    public void onLivingTick(EntityTickEvent.Pre event)
    {
        if(event.getEntity() instanceof LivingEntity living)
        {
            TickEvents.START_LIVING_ENTITY.post().handle(living);
        }
    }

    @SubscribeEvent
    public void onPlayerTickPre(PlayerTickEvent.Pre event)
    {
        TickEvents.START_PLAYER.post().handle(event.getEntity());
    }

    @SubscribeEvent
    public void onPlayerTickPost(PlayerTickEvent.Post event)
    {
        TickEvents.END_PLAYER.post().handle(event.getEntity());
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
