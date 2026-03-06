package com.mrcrayfish.framework.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mrcrayfish.framework.api.client.screen.overlay.OverlayController;
import com.mrcrayfish.framework.api.client.screen.overlay.Overlayable;
import com.mrcrayfish.framework.api.event.TickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Author: MrCrayfish
 */
@Mixin(GameRenderer.class)
public class GameRendererMixin
{
    @Inject(method = "render", at = @At(value = "HEAD"))
    private void frameworkOnPreRender(DeltaTracker timer, boolean bl, CallbackInfo ci)
    {
        TickEvents.START_RENDER.post().handle(timer);
    }

    @Inject(method = "render", at = @At(value = "TAIL"))
    private void frameworkOnPostRender(DeltaTracker timer, boolean bl, CallbackInfo ci)
    {
        TickEvents.END_RENDER.post().handle(timer);
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;renderWithTooltip(Lnet/minecraft/client/gui/GuiGraphics;IIF)V"))
    private void onRenderScreen(Screen screen, GuiGraphics graphics, int mouseX, int mouseY, float partialTick, Operation<Void> operation)
    {
        if(screen instanceof Overlayable overlayable)
        {
            // Main screen render call with a custom mouse position
            OverlayController controller = overlayable.getOverlayController();
            int overrideMouseX = controller.blocksInput() ? 1000000 : mouseX;
            int overrideMouseY = controller.blocksInput() ? 1000000 : mouseY;
            screen.render(graphics, overrideMouseX, overrideMouseY, partialTick);

            // Draw overlays
            overlayable.getOverlayController().render(graphics, mouseX, mouseY, partialTick);

            // Finally, draw deferred tooltips
            Screen.DeferredTooltipRendering deferredTooltip = screen.deferredTooltipRendering;
            if(deferredTooltip != null)
            {
                Font font = Minecraft.getInstance().font;
                graphics.renderTooltip(font, deferredTooltip.tooltip(), deferredTooltip.positioner(), mouseX, mouseY);
                screen.deferredTooltipRendering = null;
            }
        }
        else
        {
            operation.call(screen, graphics, mouseX, mouseY, partialTick);
        }
    }
}
