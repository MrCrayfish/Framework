package com.mrcrayfish.framework.mixin;

import com.mrcrayfish.framework.api.event.PlayerEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Author: MrCrayfish
 */
@Mixin(ServerPlayer.class)
public class ServerPlayerMixin
{
    @Inject(method = "die", at = @At(value = "HEAD"), cancellable = true)
    private void frameworkOnDie(DamageSource source, CallbackInfo ci)
    {
        ServerPlayer player = (ServerPlayer) (Object) this;
        if(PlayerEvents.DEATH.post().handle(player, source))
        {
            ci.cancel();
        }
    }
}
