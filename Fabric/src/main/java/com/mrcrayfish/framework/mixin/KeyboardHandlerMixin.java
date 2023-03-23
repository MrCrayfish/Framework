package com.mrcrayfish.framework.mixin;

import com.mrcrayfish.framework.api.event.InputEvents;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
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
}
