package com.mrcrayfish.framework.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mrcrayfish.framework.api.client.screen.overlay.OverlayController;
import com.mrcrayfish.framework.api.client.screen.overlay.Overlayable;
import com.mrcrayfish.framework.api.event.InputEvents;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Author: MrCrayfish
 */
@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin
{
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "keyPress", at = @At(value = "TAIL"))
    private void frameworkOnKeyEvent(long windowId, int key, int scanCode, int action, int modifiers, CallbackInfo ci)
    {
        if(windowId == this.minecraft.getWindow().getWindow())
        {
            InputEvents.KEY.post().handle(key, scanCode, action, modifiers);
        }
    }

    @WrapOperation(method = "method_1458", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/events/GuiEventListener;charTyped(CI)Z"))
    private static boolean frameworkOnCharTyped(GuiEventListener instance, char c, int modifiers, Operation<Boolean> original)
    {
        if(instance instanceof Overlayable overlayable)
        {
            OverlayController controller = overlayable.getOverlayController();
            if(controller.blocksInput())
            {
                return controller.charTyped(c, modifiers);
            }
        }
        return original.call(instance, c, modifiers);
    }

    @WrapOperation(method = "method_1473", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/events/GuiEventListener;charTyped(CI)Z"))
    private static boolean frameworkOnCharTyped2(GuiEventListener instance, char c, int modifiers, Operation<Boolean> original)
    {
        if(instance instanceof Overlayable overlayable)
        {
            OverlayController controller = overlayable.getOverlayController();
            if(controller.blocksInput())
            {
                return controller.charTyped(c, modifiers);
            }
        }
        return original.call(instance, c, modifiers);
    }
}
