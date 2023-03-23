package com.mrcrayfish.framework.mixin.client;

import com.mrcrayfish.framework.api.event.InputEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
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
        if(InputEvents.CLICK.post().handle(0, InteractionHand.MAIN_HAND))
        {
            ci.cancel();
        }
    }

    @Inject(method = "startAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/HitResult;getType()Lnet/minecraft/world/phys/HitResult$Type;"), allow = 1, cancellable = true)
    private void frameworkOnAttack(CallbackInfoReturnable<Boolean> cir)
    {
        if(InputEvents.CLICK.post().handle(0, InteractionHand.MAIN_HAND))
        {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "startUseItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;"), allow = 1, cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private void frameworkOnUse(CallbackInfo ci, InteractionHand[] var1, int var2, int var3, InteractionHand hand)
    {
        if(InputEvents.CLICK.post().handle(1, hand))
        {
            ci.cancel();
        }
    }

    @Inject(method = "pickBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getAbilities()Lnet/minecraft/world/entity/player/Abilities;"), allow = 1, cancellable = true)
    private void frameworkOnPick(CallbackInfo ci)
    {
        if(InputEvents.CLICK.post().handle(2, InteractionHand.MAIN_HAND))
        {
            ci.cancel();
        }
    }
}
