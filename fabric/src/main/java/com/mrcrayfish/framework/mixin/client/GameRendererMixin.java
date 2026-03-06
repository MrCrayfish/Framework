package com.mrcrayfish.framework.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mrcrayfish.framework.api.client.screen.overlay.OverlayController;
import com.mrcrayfish.framework.api.client.screen.overlay.Overlayable;
import com.mrcrayfish.framework.api.event.client.FrameworkClientTickEvents;
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
        FrameworkClientTickEvents.START_RENDER.post().handle(timer);
    }

    @Inject(method = "render", at = @At(value = "TAIL"))
    private void frameworkOnPostRender(DeltaTracker timer, boolean bl, CallbackInfo ci)
    {
        FrameworkClientTickEvents.END_RENDER.post().handle(timer);
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;renderWithTooltipAndSubtitles(Lnet/minecraft/client/gui/GuiGraphics;IIF)V"))
    private void onRenderScreen(Screen screen, GuiGraphics graphics, int mouseX, int mouseY, float partialTick, Operation<Void> operation)
    {
        if(screen instanceof Overlayable overlayable)
        {
            // Main screen render call with a custom mouse position
            OverlayController controller = overlayable.getOverlayController();
            int overrideMouseX = controller.blocksInput() ? 1000000 : mouseX;
            int overrideMouseY = controller.blocksInput() ? 1000000 : mouseY;

            graphics.nextStratum();
            screen.renderBackground(graphics, overrideMouseX, overrideMouseY, partialTick);
            graphics.nextStratum();
            screen.render(graphics, overrideMouseX, overrideMouseY, partialTick);
            overlayable.getOverlayController().render(graphics, mouseX, mouseY, partialTick);
            graphics.renderDeferredElements();
        }
        else
        {
            operation.call(screen, graphics, mouseX, mouseY, partialTick);
        }
    }
}
