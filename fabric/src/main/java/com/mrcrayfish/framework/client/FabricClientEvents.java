package com.mrcrayfish.framework.client;

import com.mrcrayfish.framework.api.client.screen.overlay.OverlayController;
import com.mrcrayfish.framework.api.client.screen.overlay.Overlayable;
import com.mrcrayfish.framework.api.event.FrameworkTickEvents;
import com.mrcrayfish.framework.api.event.client.FrameworkClientConnectionEvents;
import com.mrcrayfish.framework.api.event.client.FrameworkClientTickEvents;
import com.mrcrayfish.framework.api.event.client.FrameworkScreenEvents;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.components.AbstractWidget;

import java.util.Collections;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class FabricClientEvents implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            FrameworkClientTickEvents.START_CLIENT.post().handle();
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            FrameworkClientTickEvents.END_CLIENT.post().handle();
        });
        ClientTickEvents.START_LEVEL_TICK.register(level -> {
            FrameworkTickEvents.START_LEVEL.post().handle(level);
        });
        ClientTickEvents.END_LEVEL_TICK.register(level -> {
            FrameworkTickEvents.END_LEVEL.post().handle(level);
        });
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            FrameworkClientConnectionEvents.LOGGING_IN.post().handle(client.player, client.gameMode, handler.getConnection());
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            FrameworkClientConnectionEvents.LOGGING_OUT.post().handle(handler.getConnection());
        });
        net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            List<AbstractWidget> widgets = Screens.getWidgets(screen);
            FrameworkScreenEvents.INIT.post().handle(screen, Collections.unmodifiableList(widgets), widgets::add, widgets::remove);
        });
        net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.beforeExtract(screen).register((screen1, poseStack, mouseX, mouseY, partialTick) -> {
                FrameworkScreenEvents.BEFORE_EXTRACT.post().handle(screen1, poseStack, mouseX, mouseY, partialTick);
            });
            net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.afterExtract(screen).register((screen1, poseStack, mouseX, mouseY, partialTick) -> {
                FrameworkScreenEvents.AFTER_EXTRACT.post().handle(screen1, poseStack, mouseX, mouseY, partialTick);
            });
        });

        // Events for overlays
        net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents.allowMouseClick(screen).register((screen1, event) -> {
                if(screen1 instanceof Overlayable overlayable) {
                    OverlayController controller = overlayable.getOverlayController();
                    if(controller.blocksInput()) {
                        controller.mouseClicked(event, false); // TODO fix in future. fabric doesn't pass double click flag yet
                        return false;
                    }
                }
                return true;
            });
            net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents.allowMouseRelease(screen).register((screen1, event) -> {
                if(screen1 instanceof Overlayable overlayable) {
                    OverlayController controller = overlayable.getOverlayController();
                    if(controller.blocksInput()) {
                        controller.mouseReleased(event); // TODO fix in future. fabric doesn't pass double click flag yet
                        return false;
                    }
                }
                return true;
            });
            net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents.allowMouseScroll(screen).register((screen1, mouseX, mouseY, horizontalAmount, verticalAmount) -> {
                if(screen1 instanceof Overlayable overlayable) {
                    OverlayController controller = overlayable.getOverlayController();
                    if(controller.blocksInput()) {
                        controller.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
                        return false;
                    }
                }
                return true;
            });
            net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents.allowKeyPress(screen).register((screen1, event) -> {
                if(screen1 instanceof Overlayable overlayable) {
                    OverlayController controller = overlayable.getOverlayController();
                    if(controller.blocksInput()) {
                        controller.keyPressed(event);
                        return false;
                    }
                }
                return true;
            });
            net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents.allowKeyRelease(screen).register((screen1, event) -> {
                if(screen1 instanceof Overlayable overlayable) {
                    OverlayController controller = overlayable.getOverlayController();
                    if(controller.blocksInput()) {
                        controller.keyReleased(event);
                        return false;
                    }
                }
                return true;
            });
        });
    }
}
