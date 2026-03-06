package com.mrcrayfish.framework.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mrcrayfish.framework.api.client.screen.overlay.OverlayController;
import com.mrcrayfish.framework.api.client.screen.overlay.Overlayable;
import com.mrcrayfish.framework.api.event.client.FrameworkInputEvents;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
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
    private void frameworkOnKeyEvent(long windowId, int action, KeyEvent event, CallbackInfo ci)
    {
        if(windowId == this.minecraft.getWindow().handle())
        {
            FrameworkInputEvents.KEY_PRESS.post().handle(action, event);
        }
    }

    @WrapOperation(method = "charTyped", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;charTyped(Lnet/minecraft/client/input/CharacterEvent;)Z"))
    private static boolean frameworkOnCharTyped(Screen instance, CharacterEvent event, Operation<Boolean> original)
    {
        if(instance instanceof Overlayable overlayable)
        {
            OverlayController controller = overlayable.getOverlayController();
            if(controller.blocksInput())
            {
                return controller.charTyped(event);
            }
        }
        return original.call(instance, event);
    }
}
