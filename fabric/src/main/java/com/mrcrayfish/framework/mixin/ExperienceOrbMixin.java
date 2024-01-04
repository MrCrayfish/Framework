package com.mrcrayfish.framework.mixin;

import com.mrcrayfish.framework.api.event.PlayerEvents;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Author: MrCrayfish
 */
@Mixin(ExperienceOrb.class)
public class ExperienceOrbMixin
{
    @Inject(method = "playerTouch", at = @At(value = "HEAD"), cancellable = true)
    private void frameworkOnPlayerTouch(Player player, CallbackInfo ci)
    {
        ExperienceOrb orb = (ExperienceOrb) (Object) this;
        if(!orb.level().isClientSide() && player.takeXpDelay == 0)
        {
            if(PlayerEvents.PICKUP_EXPERIENCE.post().handle(player, orb))
            {
                ci.cancel();
            }
        }
    }
}
