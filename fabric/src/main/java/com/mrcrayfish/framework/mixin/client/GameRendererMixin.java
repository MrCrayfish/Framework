package com.mrcrayfish.framework.mixin.client;

import com.mrcrayfish.framework.api.event.TickEvents;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Author: MrCrayfish
 */
@Mixin(GameRenderer.class)
public class GameRendererMixin
{
    @Inject(method = "render", at = @At(value = "HEAD"))
    private void frameworkOnPreRender(float partialTick, long l, boolean bl, CallbackInfo ci)
    {
        TickEvents.START_RENDER.post().handle(partialTick);
    }

    @Inject(method = "render", at = @At(value = "TAIL"))
    private void frameworkOnPostRender(float partialTick, long l, boolean bl, CallbackInfo ci)
    {
        TickEvents.END_RENDER.post().handle(partialTick);
    }
}
