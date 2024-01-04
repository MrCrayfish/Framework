package com.mrcrayfish.framework.mixin;

import com.mrcrayfish.framework.api.event.PlayerEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Author: MrCrayfish
 */
@Mixin(ResultSlot.class)
public class ResultSlotMixin
{
    @Shadow
    @Final
    private CraftingContainer craftSlots;

    @Shadow
    @Final
    private Player player;

    @Shadow
    private int removeCount;

    @Inject(method = "checkTakeAchievements", at = @At(value = "FIELD", target = "Lnet/minecraft/world/inventory/ResultSlot;removeCount:I", ordinal = 2))
    private void frameworkOnItemCrafted(ItemStack stack, CallbackInfo ci)
    {
        if(this.removeCount > 0)
        {
            PlayerEvents.CRAFT_ITEM.post().handle(this.player, stack, this.craftSlots);
        }
    }
}
