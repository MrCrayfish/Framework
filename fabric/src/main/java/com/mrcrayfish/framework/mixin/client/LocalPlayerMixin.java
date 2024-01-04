package com.mrcrayfish.framework.mixin.client;

import com.mrcrayfish.framework.api.event.ClientEvents;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Author: MrCrayfish
 */
@Mixin(LocalPlayer.class)
public class LocalPlayerMixin
{
    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/tutorial/Tutorial;onInput(Lnet/minecraft/client/player/Input;)V"))
    private void frameworkOnInput(CallbackInfo ci)
    {
        LocalPlayer player = (LocalPlayer) (Object) this;
        ClientEvents.PLAYER_INPUT_UPDATE.post().handle(player, player.input);
    }
}
