package com.mrcrayfish.framework.client;

import com.mrcrayfish.framework.api.event.ClientConnectionEvents;
import com.mrcrayfish.framework.api.event.ClientEvents;
import com.mrcrayfish.framework.api.event.InputEvents;
import com.mrcrayfish.framework.api.event.ScreenEvents;
import com.mrcrayfish.framework.api.event.TickEvents;
import com.mrcrayfish.framework.config.FrameworkConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: MrCrayfish
 */
public class ClientForgeEvents
{
    @SubscribeEvent
    public void onClientPlayerLoggingIn(ClientPlayerNetworkEvent.LoggedInEvent event)
    {
        FrameworkConfigManager.getInstance().loadDefaultSyncConfigsIfUnloaded();
        ClientConnectionEvents.LOGGING_IN.post().handle(event.getPlayer(), event.getMultiPlayerGameMode(), event.getConnection());
    }

    @SubscribeEvent
    public void onClientPlayerLoggingOut(ClientPlayerNetworkEvent.LoggedOutEvent event)
    {
        ClientConnectionEvents.LOGGING_OUT.post().handle(event.getConnection());
    }

    @SubscribeEvent
    public void onAfterDrawBackground(ContainerScreenEvent.DrawBackground event)
    {
        ScreenEvents.AFTER_DRAW_CONTAINER_BACKGROUND.post().handle(event.getContainerScreen(), event.getPoseStack(), event.getMouseX(), event.getMouseY());
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
    public void onKey(InputEvent.KeyInputEvent event)
    {
        InputEvents.KEY.post().handle(event.getKey(), event.getScanCode(), event.getAction(), event.getModifiers());
    }

    @SubscribeEvent
    public void onInteraction(InputEvent.ClickInputEvent event)
    {
        if(InputEvents.CLICK.post().handle(event.isAttack(), event.isUseItem(), event.isPickBlock(), event.getHand()))
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onScreenInit(ScreenEvent.InitScreenEvent.Post event)
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
    public void onScreenRenderPre(ScreenEvent.DrawScreenEvent.Pre event)
    {
        ScreenEvents.BEFORE_DRAW.post().handle(event.getScreen(), event.getPoseStack(), event.getMouseX(), event.getMouseY(), event.getPartialTicks());
    }

    @SubscribeEvent
    public void onScreenRenderPost(ScreenEvent.DrawScreenEvent.Post event)
    {
        ScreenEvents.AFTER_DRAW.post().handle(event.getScreen(), event.getPoseStack(), event.getMouseX(), event.getMouseY(), event.getPartialTicks());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST) // Lowest means last, if called unlikely been cancelled
    public void onScreenOpen(ScreenOpenEvent event)
    {
        if(event.getScreen() != null)
        {
            ScreenEvents.OPENED.post().handle(event.getScreen());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST) // Lowest means last, if called unlikely been cancelled
    public void onScreenClosed(ScreenOpenEvent event)
    {
        if(event.getScreen() == null)
        {
            Screen old = Minecraft.getInstance().screen;
            ScreenEvents.CLOSED.post().handle(old);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onInputUpdate(MovementInputUpdateEvent event)
    {
        ClientEvents.PLAYER_INPUT_UPDATE.post().handle(event.getPlayer(), event.getInput());
    }
}
