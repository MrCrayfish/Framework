package com.mrcrayfish.framework.mixin.client;

import com.mrcrayfish.framework.api.event.client.FrameworkScreenEvents;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Author: MrCrayfish
 */
@Mixin(Screen.class)
public class ScreenMixin
{
    @Inject(method = "Lnet/minecraft/client/gui/screens/Screen;extractRenderStateWithTooltipAndSubtitles(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;nextStratum()V", ordinal = 1))
    private void frameworkAfterDrawBackground(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick, CallbackInfo ci)
    {
        FrameworkScreenEvents.AFTER_EXTRACT_BACKGROUND.post().handle((Screen) (Object) this, extractor, mouseX, mouseY, partialTick);
    }
}
