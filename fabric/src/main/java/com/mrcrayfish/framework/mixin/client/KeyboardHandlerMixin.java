package com.mrcrayfish.framework.mixin.client;

import com.mrcrayfish.framework.api.event.client.FrameworkInputEvents;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
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
            FrameworkInputEvents.KEY.post().handle(event.key(), event.scancode(), action, event.modifiers());
            FrameworkInputEvents.KEY_PRESS.post().handle(action, event);
        }
    }
}
