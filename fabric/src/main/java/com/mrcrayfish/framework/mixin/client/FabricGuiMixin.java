package com.mrcrayfish.framework.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mrcrayfish.framework.api.client.screen.overlay.OverlayController;
import com.mrcrayfish.framework.api.client.screen.overlay.Overlayable;
import com.mrcrayfish.framework.api.event.client.FrameworkScreenEvents;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * Author: MrCrayfish
 */
@Mixin(Gui.class)
public class FabricGuiMixin
{
    @Inject(method = "setScreen", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/Gui;screen:Lnet/minecraft/client/gui/screens/Screen;", opcode = Opcodes.PUTFIELD), locals = LocalCapture.CAPTURE_FAILHARD)
    private void frameworkOnScreenAdded(Screen screen, CallbackInfo ci)
    {
        FrameworkScreenEvents.OPENED.post().handle(screen);
    }

    @Inject(method = "setScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;removed()V"))
    private void frameworkOnScreenClosed(Screen screen, CallbackInfo ci)
    {
        Gui gui = (Gui) (Object) this;
        FrameworkScreenEvents.CLOSED.post().handle(gui.screen());

        if(screen instanceof Overlayable overlayable)
        {
            OverlayController controller = overlayable.getOverlayController();
            controller.closeAll();
        }
    }

    // Moved from GameRendererMixin: in MC 26.2 the screen extraction call moved from
    // GameRenderer.extractGui into Gui.extractRenderState.
    @WrapOperation(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;extractRenderStateWithTooltipAndSubtitles(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V"))
    private void onRenderScreen(Screen screen, GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick, Operation<Void> operation)
    {
        if(screen instanceof Overlayable overlayable)
        {
            // Main screen render call with a custom mouse position
            OverlayController controller = overlayable.getOverlayController();
            int overrideMouseX = controller.blocksInput() ? 1000000 : mouseX;
            int overrideMouseY = controller.blocksInput() ? 1000000 : mouseY;

            extractor.nextStratum();
            screen.extractBackground(extractor, overrideMouseX, overrideMouseY, partialTick);
            extractor.nextStratum();
            screen.extractRenderState(extractor, overrideMouseX, overrideMouseY, partialTick);
            overlayable.getOverlayController().render(extractor, mouseX, mouseY, partialTick);
            extractor.extractDeferredElements(mouseX, mouseY, partialTick);
        }
        else
        {
            operation.call(screen, extractor, mouseX, mouseY, partialTick);
        }
    }
}
