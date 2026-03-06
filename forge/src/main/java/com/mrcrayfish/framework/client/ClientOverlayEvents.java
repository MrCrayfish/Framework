package com.mrcrayfish.framework.client;

import com.mrcrayfish.framework.api.client.screen.overlay.OverlayController;
import com.mrcrayfish.framework.api.client.screen.overlay.Overlayable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class ClientOverlayEvents
{
    @SubscribeEvent
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
            screen.render(event.getGuiGraphics(), overrideMouseX, overrideMouseY, event.getPartialTick());

            // Draw overlays
            overlayable.getOverlayController().render(event.getGuiGraphics(), event.getMouseX(), event.getMouseY(), event.getPartialTick());

            // Finally, draw deferred tooltips
            Screen.DeferredTooltipRendering deferredTooltip = screen.deferredTooltipRendering;
            if(deferredTooltip != null)
            {
                Font font = Minecraft.getInstance().font;
                event.getGuiGraphics().renderTooltip(font, deferredTooltip.tooltip(), deferredTooltip.positioner(), event.getMouseX(), event.getMouseY());
                screen.deferredTooltipRendering = null;
            }
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
                controller.mouseClicked(event.getMouseX(), event.getMouseY(), event.getButton());
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
                controller.mouseReleased(event.getMouseX(), event.getMouseY(), event.getButton());
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
                controller.keyPressed(event.getKeyCode(), event.getScanCode(), event.getModifiers());
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
                controller.keyReleased(event.getKeyCode(), event.getScanCode(), event.getModifiers());
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
                controller.charTyped(event.getCodePoint(), event.getModifiers());
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
                controller.mouseScrolled(event.getMouseX(), event.getMouseY(), event.getDeltaX(), event.getDeltaY());
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
                controller.mouseDragged(event.getMouseX(), event.getMouseY(), event.getMouseButton(), event.getDragX(), event.getDragY());
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
