package com.mrcrayfish.framework.mixin.client;

import com.mrcrayfish.framework.platform.Services;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
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
        Services.REGISTRATION.getRegistryObjects(KeyMapping.class).forEach(KeyMappingHelper::registerKeyMapping);
    }
}
