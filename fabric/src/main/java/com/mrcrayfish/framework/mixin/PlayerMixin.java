package com.mrcrayfish.framework.mixin;

import com.mrcrayfish.framework.api.event.FrameworkPlayerEvents;
import com.mrcrayfish.framework.api.event.FrameworkTickEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Author: MrCrayfish
 */
@Mixin(Player.class)
public class PlayerMixin
{
    @Inject(method = "die", at = @At(value = "HEAD"), cancellable = true)
    private void frameworkOnDie(DamageSource source, CallbackInfo ci)
    {
        Player player = (Player) (Object) this;
        if(FrameworkPlayerEvents.DEATH.post().handle(player, source))
        {
            ci.cancel();
        }
    }

    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void frameworkOnPreTick(CallbackInfo ci)
    {
        FrameworkTickEvents.START_PLAYER.post().handle((Player) (Object) this);
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    private void frameworkOnPostTick(CallbackInfo ci)
    {
        FrameworkTickEvents.END_PLAYER.post().handle((Player) (Object) this);
    }
}
