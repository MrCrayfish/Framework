package com.mrcrayfish.framework.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.framework.api.event.ScreenEvents;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * Author: MrCrayfish
 */
@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin
{
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;disableDepthTest()V", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
    private void frameworkAfterDrawBackground(PoseStack poseStack, int mouseX, int mouseY, float partialTick, CallbackInfo ci, int left, int top)
    {
        ScreenEvents.AFTER_DRAW_CONTAINER_BACKGROUND.post().handle((AbstractContainerScreen<?>) (Object) this, poseStack, mouseX, mouseY);
    }
}
