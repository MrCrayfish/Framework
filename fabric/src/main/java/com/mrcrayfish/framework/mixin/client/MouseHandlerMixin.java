package com.mrcrayfish.framework.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mrcrayfish.framework.api.client.screen.overlay.OverlayController;
import com.mrcrayfish.framework.api.client.screen.overlay.Overlayable;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin
{
    @WrapOperation(method = "method_55795", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;mouseDragged(DDIDD)Z"))
    private static boolean frameworkOnMouseDragged(Screen instance, double mouseX, double mouseY, int button, double dragX, double dragY, Operation<Boolean> original)
    {
        if(instance instanceof Overlayable overlayable)
        {
            OverlayController controller = overlayable.getOverlayController();
            if(controller.blocksInput())
            {
                return controller.mouseDragged(mouseX, mouseY, button, dragX, dragY);
            }
        }
        return original.call(instance, mouseX, mouseY, button, dragX, dragY);
    }
}
