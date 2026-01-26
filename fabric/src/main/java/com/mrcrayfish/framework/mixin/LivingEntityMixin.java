package com.mrcrayfish.framework.mixin;

import com.mrcrayfish.framework.api.event.EntityEvents;
import com.mrcrayfish.framework.api.event.TickEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Author: MrCrayfish
 */
@Mixin(LivingEntity.class)
public class LivingEntityMixin
{
    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void frameworkOnPreTick(CallbackInfo ci)
    {
        TickEvents.START_LIVING_ENTITY.post().handle((LivingEntity) (Object) this);
    }
}
