package com.mrcrayfish.framework.client;

import com.mrcrayfish.framework.api.client.screen.overlay.OverlayController;
import com.mrcrayfish.framework.api.client.screen.overlay.Overlayable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;

public final class ClientOverlayEvents
{
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onScreenRender(ScreenEvent.Render.Pre event)
    {
        Screen screen = event.getScreen();
        if(screen instanceof Overlayable overlayable)
        {
            // We do custom rendering for overlayable screens, so cancel default
            event.setCanceled(true);

            // Main screen render call with a custom mouse position
            OverlayController controller = overlayable.getOverlayController();
            int overrideMouseX = controller.blocksInput() ? 1000000 : event.getMouseX();
            int overrideMouseY = controller.blocksInput() ? 1000000 : event.getMouseY();

            GuiGraphics graphics = event.getGuiGraphics();
            graphics.nextStratum();
            screen.renderBackground(graphics, overrideMouseX, overrideMouseY, event.getPartialTick());
            graphics.nextStratum();
            screen.render(graphics, overrideMouseX, overrideMouseY, event.getPartialTick());
            overlayable.getOverlayController().render(graphics, event.getMouseX(), event.getMouseY(), event.getPartialTick());
            graphics.renderDeferredElements();
        }
    }

    @SubscribeEvent
    public void onMousePressed(ScreenEvent.MouseButtonPressed.Pre event)
    {
        if(event.getScreen() instanceof Overlayable overlayable)
        {
            OverlayController controller = overlayable.getOverlayController();
            if(controller.blocksInput())
            {
                controller.mouseClicked(event.getMouseButtonEvent(), event.isDoubleClick());
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onMouseReleased(ScreenEvent.MouseButtonReleased.Pre event)
    {
        if(event.getScreen() instanceof Overlayable overlayable)
        {
            OverlayController controller = overlayable.getOverlayController();
            if(controller.blocksInput())
            {
                controller.mouseReleased(event.getMouseButtonEvent());
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onKeyPressed(ScreenEvent.KeyPressed.Pre event)
    {
        if(event.getScreen() instanceof Overlayable overlayable)
        {
            OverlayController controller = overlayable.getOverlayController();
            if(controller.blocksInput())
            {
                controller.keyPressed(event.getKeyEvent());
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onKeyReleased(ScreenEvent.KeyReleased.Pre event)
    {
        if(event.getScreen() instanceof Overlayable overlayable)
        {
            OverlayController controller = overlayable.getOverlayController();
            if(controller.blocksInput())
            {
                controller.keyReleased(event.getKeyEvent());
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onCharTyped(ScreenEvent.CharacterTyped.Pre event)
    {
        if(event.getScreen() instanceof Overlayable overlayable)
        {
            OverlayController controller = overlayable.getOverlayController();
            if(controller.blocksInput())
            {
                controller.charTyped(event.getCharacterEvent());
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onMouseScrolled(ScreenEvent.MouseScrolled.Pre event)
    {
        if(event.getScreen() instanceof Overlayable overlayable)
        {
            OverlayController controller = overlayable.getOverlayController();
            if(controller.blocksInput())
            {
                controller.mouseScrolled(event.getMouseX(), event.getMouseY(), event.getScrollDeltaX(), event.getScrollDeltaY());
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onMouseDragged(ScreenEvent.MouseDragged.Pre event)
    {
        if(event.getScreen() instanceof Overlayable overlayable)
        {
            OverlayController controller = overlayable.getOverlayController();
            if(controller.blocksInput())
            {
                controller.mouseDragged(event.getMouseButtonEvent(), event.getDragX(), event.getDragY());
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onScreenClosing(ScreenEvent.Closing event)
    {
        if(event.getScreen() instanceof Overlayable overlayable)
        {
            OverlayController controller = overlayable.getOverlayController();
            controller.closeAll();
        }
    }
}
