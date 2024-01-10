package com.mrcrayfish.framework.client;

import com.mrcrayfish.framework.api.event.ClientConnectionEvents;
import com.mrcrayfish.framework.api.event.ClientEvents;
import com.mrcrayfish.framework.api.event.InputEvents;
import com.mrcrayfish.framework.api.event.ScreenEvents;
import com.mrcrayfish.framework.api.event.TickEvents;
import net.minecraft.client.gui.components.AbstractWidget;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ContainerScreenEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.TickEvent;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public class ClientNeoForgeEvents
{
    @SubscribeEvent
    public void onClientPlayerLoggingIn(ClientPlayerNetworkEvent.LoggingIn event)
    {
        ClientConnectionEvents.LOGGING_IN.post().handle(event.getPlayer(), event.getMultiPlayerGameMode(), event.getConnection());
    }

    @SubscribeEvent
    public void onClientPlayerLoggingOut(ClientPlayerNetworkEvent.LoggingOut event)
    {
        ClientConnectionEvents.LOGGING_OUT.post().handle(event.getConnection());
    }

    @SubscribeEvent
    public void onAfterDrawBackground(ContainerScreenEvent.Render.Background event)
    {
        ScreenEvents.AFTER_DRAW_CONTAINER_BACKGROUND.post().handle(event.getContainerScreen(), event.getGuiGraphics(), event.getMouseX(), event.getMouseY());
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.ClientTickEvent event)
    {
        if(event.phase == TickEvent.Phase.START)
        {
            TickEvents.START_CLIENT.post().handle();
        }
        else
        {
            TickEvents.END_CLIENT.post().handle();
        }
    }

    @SubscribeEvent
    public void onKey(InputEvent.Key event)
    {
        InputEvents.KEY.post().handle(event.getKey(), event.getScanCode(), event.getAction(), event.getModifiers());
    }

    @SubscribeEvent
    public void onInteraction(InputEvent.InteractionKeyMappingTriggered event)
    {
        if(InputEvents.CLICK.post().handle(event.isAttack(), event.isUseItem(), event.isPickBlock(), event.getHand()))
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onScreenInit(ScreenEvent.Init.Post event)
    {
        ScreenEvents.INIT.post().handle(event.getScreen());
        List<AbstractWidget> widgets = event.getListenersList().stream().filter(listener -> listener instanceof AbstractWidget).map(listener -> (AbstractWidget) listener).toList();
        ScreenEvents.MODIFY_WIDGETS.post().handle(event.getScreen(), widgets, event::addListener, event::removeListener);
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event)
    {
        if(event.phase == TickEvent.Phase.START)
        {
            TickEvents.START_RENDER.post().handle(event.renderTickTime);
        }
        else
        {
            TickEvents.END_RENDER.post().handle(event.renderTickTime);
        }
    }

    @SubscribeEvent
    public void onScreenRenderPre(ScreenEvent.Render.Pre event)
    {
        ScreenEvents.BEFORE_DRAW.post().handle(event.getScreen(), event.getGuiGraphics(), event.getMouseX(), event.getMouseY(), event.getPartialTick());
    }

    @SubscribeEvent
    public void onScreenRenderPost(ScreenEvent.Render.Post event)
    {
        ScreenEvents.AFTER_DRAW.post().handle(event.getScreen(), event.getGuiGraphics(), event.getMouseX(), event.getMouseY(), event.getPartialTick());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST) // Lowest means last, if called unlikely been cancelled
    public void onScreenOpen(ScreenEvent.Opening event)
    {
        ScreenEvents.OPENED.post().handle(event.getNewScreen());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST) // Lowest means last, if called unlikely been cancelled
    public void onScreenOpen(ScreenEvent.Closing event)
    {
        ScreenEvents.CLOSED.post().handle(event.getScreen());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onInputUpdate(MovementInputUpdateEvent event)
    {
        ClientEvents.PLAYER_INPUT_UPDATE.post().handle(event.getEntity(), event.getInput());
    }
}
