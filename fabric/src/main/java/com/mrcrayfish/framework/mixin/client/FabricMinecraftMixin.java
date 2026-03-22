package com.mrcrayfish.framework.mixin.client;

import com.mrcrayfish.framework.api.client.screen.overlay.OverlayController;
import com.mrcrayfish.framework.api.client.screen.overlay.Overlayable;
import com.mrcrayfish.framework.api.event.client.FrameworkInputEvents;
import com.mrcrayfish.framework.api.event.client.FrameworkScreenEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.InteractionHand;
import org.objectweb.asm.Opcodes;
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
public class FabricMinecraftMixin
{
    @Inject(method = "continueAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/BlockHitResult;getDirection()Lnet/minecraft/core/Direction;"), allow = 1, cancellable = true)
    private void frameworkOnContinue(boolean bl, CallbackInfo ci)
    {
        if(FrameworkInputEvents.INTERACTION.post().handle(true, false, false, InteractionHand.MAIN_HAND))
        {
            ci.cancel();
        }
    }

    @Inject(method = "startAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/HitResult;getType()Lnet/minecraft/world/phys/HitResult$Type;"), allow = 1, cancellable = true)
    private void frameworkOnAttack(CallbackInfoReturnable<Boolean> cir)
    {
        if(FrameworkInputEvents.INTERACTION.post().handle(true, false, false, InteractionHand.MAIN_HAND))
        {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "startUseItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;"), allow = 1, cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private void frameworkOnUse(CallbackInfo ci, InteractionHand[] var1, int var2, int var3, InteractionHand hand)
    {
        if(FrameworkInputEvents.INTERACTION.post().handle(false, true, false, hand))
        {
            ci.cancel();
        }
    }

    @Inject(method = "pickBlockOrEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;hasControlDown()Z"), allow = 1, cancellable = true)
    private void frameworkOnPick(CallbackInfo ci)
    {
        if(FrameworkInputEvents.INTERACTION.post().handle(false, false, true, InteractionHand.MAIN_HAND))
        {
            ci.cancel();
        }
    }

    @Inject(method = "setScreen", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;screen:Lnet/minecraft/client/gui/screens/Screen;", opcode = Opcodes.PUTFIELD), locals = LocalCapture.CAPTURE_FAILHARD)
    private void frameworkOnScreenAdded(Screen screen, CallbackInfo ci)
    {
        FrameworkScreenEvents.OPENED.post().handle(screen);
    }

    @Inject(method = "setScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;removed()V"))
    private void frameworkOnScreenClosed(Screen screen, CallbackInfo ci)
    {
        Minecraft mc = (Minecraft) (Object) this;
        FrameworkScreenEvents.CLOSED.post().handle(mc.screen);

        if(screen instanceof Overlayable overlayable)
        {
            OverlayController controller = overlayable.getOverlayController();
            controller.closeAll();
        }
    }
}
