package com.mrcrayfish.framework.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mrcrayfish.framework.api.client.screen.overlay.OverlayController;
import com.mrcrayfish.framework.api.client.screen.overlay.Overlayable;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin
{
    @WrapOperation(method = "handleAccumulatedMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;mouseDragged(Lnet/minecraft/client/input/MouseButtonEvent;DD)Z"))
    private static boolean frameworkOnMouseDragged(Screen instance, MouseButtonEvent event, double dragX, double dragY, Operation<Boolean> original)
    {
        if(instance instanceof Overlayable overlayable)
        {
            OverlayController controller = overlayable.getOverlayController();
            if(controller.blocksInput())
            {
                return controller.mouseDragged(event, dragX, dragY);
            }
        }
        return original.call(instance, event, dragX, dragY);
    }
}
