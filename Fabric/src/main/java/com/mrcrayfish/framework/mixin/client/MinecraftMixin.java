package com.mrcrayfish.framework.mixin.client;

import com.mrcrayfish.framework.api.event.InputEvents;
import com.mrcrayfish.framework.api.event.ScreenEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.InteractionHand;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * Author: MrCrayfish
 */
@Mixin(Minecraft.class)
public class MinecraftMixin
{
    @Inject(method = "continueAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/BlockHitResult;getDirection()Lnet/minecraft/core/Direction;"), allow = 1, cancellable = true)
    private void frameworkOnContinue(boolean bl, CallbackInfo ci)
    {
        if(InputEvents.CLICK.post().handle(true, false, false, InteractionHand.MAIN_HAND))
        {
            ci.cancel();
        }
    }

    @Inject(method = "startAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/HitResult;getType()Lnet/minecraft/world/phys/HitResult$Type;"), allow = 1, cancellable = true)
    private void frameworkOnAttack(CallbackInfoReturnable<Boolean> cir)
    {
        if(InputEvents.CLICK.post().handle(true, false, false, InteractionHand.MAIN_HAND))
        {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "startUseItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;"), allow = 1, cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private void frameworkOnUse(CallbackInfo ci, InteractionHand[] var1, int var2, int var3, InteractionHand hand)
    {
        if(InputEvents.CLICK.post().handle(false, true, false, hand))
        {
            ci.cancel();
        }
    }

    @Inject(method = "pickBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getAbilities()Lnet/minecraft/world/entity/player/Abilities;"), allow = 1, cancellable = true)
    private void frameworkOnPick(CallbackInfo ci)
    {
        if(InputEvents.CLICK.post().handle(false, false, true, InteractionHand.MAIN_HAND))
        {
            ci.cancel();
        }
    }

    @Inject(method = "setScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;added()V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void frameworkOnScreenAdded(Screen screen, CallbackInfo ci)
    {
        ScreenEvents.OPENED.post().handle(screen);
    }
}
