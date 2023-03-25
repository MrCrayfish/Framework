package com.mrcrayfish.framework.mixin.client;

import com.mrcrayfish.framework.api.event.InputEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

/**
 * Author: MrCrayfish
 */
@Mixin(Options.class)
public class OptionsMixin
{
    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;load()V"))
    private void frameworkInit(Minecraft minecraft, File file, CallbackInfo ci)
    {
        InputEvents.REGISTER_KEY_MAPPING.post().handle(KeyBindingHelper::registerKeyBinding);
    }
}
