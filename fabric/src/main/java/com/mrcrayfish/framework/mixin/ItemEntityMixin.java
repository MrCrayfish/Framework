package com.mrcrayfish.framework.mixin;

import com.mrcrayfish.framework.api.event.PlayerEvents;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Author: MrCrayfish
 */
@Mixin(ItemEntity.class)
public class ItemEntityMixin
{
    @Inject(method = "playerTouch", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;getItem()Lnet/minecraft/world/item/ItemStack;"), allow = 1, cancellable = true)
    private void frameworkOnPlayerTouch(Player player, CallbackInfo ci)
    {
        ItemEntity entity = (ItemEntity) (Object) this;
        if(PlayerEvents.PICKUP_ITEM.post().handle(player, entity))
        {
            ci.cancel();
        }
    }
}
