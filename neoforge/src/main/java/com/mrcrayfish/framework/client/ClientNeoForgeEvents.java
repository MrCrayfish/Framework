package com.mrcrayfish.framework.client;

import com.mrcrayfish.framework.api.event.client.FrameworkClientConnectionEvents;
import com.mrcrayfish.framework.api.event.client.FrameworkClientTickEvents;
import com.mrcrayfish.framework.api.event.client.FrameworkInputEvents;
import com.mrcrayfish.framework.api.event.client.FrameworkScreenEvents;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.*;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public class ClientNeoForgeEvents
{
    @SubscribeEvent
    public void onClientPlayerLoggingIn(ClientPlayerNetworkEvent.LoggingIn event)
    {
        FrameworkClientConnectionEvents.LOGGING_IN.post().handle(event.getPlayer(), event.getMultiPlayerGameMode(), event.getConnection());
    }

    @SubscribeEvent
    public void onClientPlayerLoggingOut(ClientPlayerNetworkEvent.LoggingOut event)
    {
        FrameworkClientConnectionEvents.LOGGING_OUT.post().handle(event.getConnection());
    }

    @SubscribeEvent
    public void onAfterDrawBackground(ScreenEvent.Render.Background event)
    {
        if(event.getScreen() instanceof ContainerScreen screen)
        {
            FrameworkScreenEvents.AFTER_DRAW_CONTAINER_BACKGROUND.post().handle(screen, event.getGuiGraphics(), event.getMouseX(), event.getMouseY());
        }
    }

    @SubscribeEvent
    public void onClientTickPre(ClientTickEvent.Pre event)
    {
        FrameworkClientTickEvents.START_CLIENT.post().handle();
    }

    @SubscribeEvent
    public void onClientTickPost(ClientTickEvent.Post event)
    {
        FrameworkClientTickEvents.END_CLIENT.post().handle();
    }

    @SubscribeEvent
    public void onKey(InputEvent.Key event)
    {
        FrameworkInputEvents.KEY_PRESS.post().handle(event.getAction(), event.getKeyEvent());
    }

    @SubscribeEvent
    public void onInteraction(InputEvent.InteractionKeyMappingTriggered event)
    {
        if(FrameworkInputEvents.INTERACTION.post().handle(event.isAttack(), event.isUseItem(), event.isPickBlock(), event.getHand()))
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onScreenInit(ScreenEvent.Init.Post event)
    {
        List<AbstractWidget> widgets = event.getListenersList().stream().filter(listener -> listener instanceof AbstractWidget).map(listener -> (AbstractWidget) listener).toList();
        FrameworkScreenEvents.INIT.post().handle(event.getScreen(), widgets, event::addListener, event::removeListener);
    }

    @SubscribeEvent
    public void onRenderFramePre(RenderFrameEvent.Pre event)
    {
        FrameworkClientTickEvents.START_RENDER.post().handle(event.getPartialTick());
    }

    @SubscribeEvent
    public void onRenderFramePost(RenderFrameEvent.Post event)
    {
        FrameworkClientTickEvents.END_RENDER.post().handle(event.getPartialTick());
    }

    @SubscribeEvent
    public void onScreenRenderPre(ScreenEvent.Render.Pre event)
    {
        FrameworkScreenEvents.BEFORE_DRAW.post().handle(event.getScreen(), event.getGuiGraphics(), event.getMouseX(), event.getMouseY(), event.getPartialTick());
    }

    @SubscribeEvent
    public void onScreenRenderPost(ScreenEvent.Render.Post event)
    {
        FrameworkScreenEvents.AFTER_DRAW.post().handle(event.getScreen(), event.getGuiGraphics(), event.getMouseX(), event.getMouseY(), event.getPartialTick());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST) // Lowest means last, if called unlikely been cancelled
    public void onScreenOpen(ScreenEvent.Opening event)
    {
        FrameworkScreenEvents.OPENED.post().handle(event.getNewScreen());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST) // Lowest means last, if called unlikely been cancelled
    public void onScreenOpen(ScreenEvent.Closing event)
    {
        FrameworkScreenEvents.CLOSED.post().handle(event.getScreen());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onInputUpdate(MovementInputUpdateEvent event)
    {
        FrameworkInputEvents.CLIENT_INPUT_UPDATE.post().handle(event.getEntity(), event.getInput());
    }
}
