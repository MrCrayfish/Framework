package com.mrcrayfish.framework.event;

import com.mrcrayfish.framework.api.event.FrameworkEntityEvents;
import com.mrcrayfish.framework.api.event.FrameworkPlayerEvents;
import com.mrcrayfish.framework.api.event.FrameworkServerEvents;
import com.mrcrayfish.framework.api.event.FrameworkTickEvents;
import com.mrcrayfish.framework.config.ConfigWatcher;
import net.minecraft.util.TriState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.GameShuttingDownEvent;
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
        FrameworkTickEvents.START_SERVER.post().handle(event.getServer());
    }

    @SubscribeEvent
    public void onServerTickPost(ServerTickEvent.Post event)
    {
        FrameworkTickEvents.END_SERVER.post().handle(event.getServer());
    }

    @SubscribeEvent
    public void onLevelTickPre(LevelTickEvent.Pre event)
    {
        FrameworkTickEvents.START_LEVEL.post().handle(event.getLevel());
    }

    @SubscribeEvent
    public void onLevelTickPost(LevelTickEvent.Post event)
    {
        FrameworkTickEvents.END_LEVEL.post().handle(event.getLevel());
    }

    @SubscribeEvent
    public void onStartTracking(PlayerEvent.StartTracking event)
    {
        FrameworkPlayerEvents.STARTED_TRACKING_ENTITY.post().handle(event.getTarget(), event.getEntity());
    }

    @SubscribeEvent
    public void onStartTracking(PlayerEvent.StopTracking event)
    {
        FrameworkPlayerEvents.STOPPED_TRACKING_ENTITY.post().handle(event.getTarget(), event.getEntity());
    }

    @SubscribeEvent
    public void onEntityJoinLevel(EntityJoinLevelEvent event)
    {
        FrameworkEntityEvents.JOIN_LEVEL.post().handle(event.getEntity(), event.getLevel(), event.loadedFromDisk());
    }

    @SubscribeEvent
    public void onEntityLeaveLevel(EntityLeaveLevelEvent event)
    {
        FrameworkEntityEvents.LEAVE_LEVEL.post().handle(event.getEntity(), event.getLevel());
    }

    @SubscribeEvent
    public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event)
    {
        FrameworkPlayerEvents.CHANGE_DIMENSION.post().handle(event.getEntity(), event.getFrom(), event.getTo());
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.Clone event)
    {
        FrameworkPlayerEvents.COPY.post().handle(event.getOriginal(), event.getEntity(), !event.isWasDeath());
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event)
    {
        FrameworkPlayerEvents.RESPAWN.post().handle(event.getEntity(), event.isEndConquered());
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        FrameworkPlayerEvents.LOGGED_IN.post().handle(event.getEntity());
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedOutEvent event)
    {
        FrameworkPlayerEvents.LOGGED_OUT.post().handle(event.getEntity());
    }

    @SubscribeEvent
    public void onPickupItem(ItemEntityPickupEvent.Pre event)
    {
        if(FrameworkPlayerEvents.PICKUP_ITEM.post().handle(event.getPlayer(), event.getItemEntity()))
        {
            event.setCanPickup(TriState.FALSE);
        }
    }

    @SubscribeEvent
    public void onCraftItem(PlayerEvent.ItemCraftedEvent event)
    {
        FrameworkPlayerEvents.CRAFTED_ITEM.post().handle(event.getEntity(), event.getCrafting(), event.getInventory());
    }

    @SubscribeEvent
    public void onLivingEntityDeath(LivingDeathEvent event)
    {
        if(event.getEntity() instanceof Player player)
        {
            if(FrameworkPlayerEvents.DEATH.post().handle(player, event.getSource()))
            {
                event.setCanceled(true);
            }
        }

        if(FrameworkEntityEvents.LIVING_ENTITY_DEATH.post().handle(event.getEntity(), event.getSource()))
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPlayerPickupExp(PlayerXpEvent.PickupXp event)
    {
        if(FrameworkPlayerEvents.PICKUP_EXPERIENCE.post().handle(event.getEntity(), event.getOrb()))
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onLivingTick(EntityTickEvent.Pre event)
    {
        if(event.getEntity() instanceof LivingEntity living)
        {
            FrameworkTickEvents.START_LIVING_ENTITY.post().handle(living);
        }
    }

    @SubscribeEvent
    public void onLivingTick(EntityTickEvent.Post event)
    {
        if(event.getEntity() instanceof LivingEntity living)
        {
            FrameworkTickEvents.END_LIVING_ENTITY.post().handle(living);
        }
    }

    @SubscribeEvent
    public void onPlayerTickPre(PlayerTickEvent.Pre event)
    {
        FrameworkTickEvents.START_PLAYER.post().handle(event.getEntity());
    }

    @SubscribeEvent
    public void onPlayerTickPost(PlayerTickEvent.Post event)
    {
        FrameworkTickEvents.END_PLAYER.post().handle(event.getEntity());
    }

    @SubscribeEvent
    public void onServerStarting(ServerAboutToStartEvent event)
    {
        FrameworkServerEvents.STARTING.post().handle(event.getServer());
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event)
    {
        FrameworkServerEvents.STARTED.post().handle(event.getServer());
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event)
    {
        FrameworkServerEvents.STOPPING.post().handle(event.getServer());
    }

    @SubscribeEvent
    public void onServerStopped(ServerStoppedEvent event)
    {
        FrameworkServerEvents.STOPPED.post().handle(event.getServer());
    }

    @SubscribeEvent
    public void onShuttingDown(GameShuttingDownEvent event)
    {
        ConfigWatcher.get().stop();
    }
}
