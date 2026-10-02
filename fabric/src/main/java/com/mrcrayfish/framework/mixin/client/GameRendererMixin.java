package com.mrcrayfish.framework.mixin.client;

import com.mrcrayfish.framework.api.event.client.FrameworkClientTickEvents;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Author: MrCrayfish
 */
@Mixin(GameRenderer.class)
public class GameRendererMixin
{
    @Final
    @Shadow
    private Minecraft minecraft;

    @Inject(method = "render", at = @At(value = "HEAD"))
    private void frameworkOnPreRender(CallbackInfo ci)
    {
        FrameworkClientTickEvents.START_RENDER.post().handle(this.minecraft.getDeltaTracker());
    }

    @Inject(method = "render", at = @At(value = "TAIL"))
    private void frameworkOnPostRender(CallbackInfo ci)
    {
        FrameworkClientTickEvents.END_RENDER.post().handle(this.minecraft.getDeltaTracker());
    }
}
