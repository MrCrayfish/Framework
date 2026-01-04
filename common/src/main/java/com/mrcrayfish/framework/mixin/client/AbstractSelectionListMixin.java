package com.mrcrayfish.framework.mixin.client;

import com.mrcrayfish.framework.api.client.screen.widget.FrameworkSelectionList;
import net.minecraft.client.gui.components.AbstractSelectionList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSelectionList.class)
public class AbstractSelectionListMixin
{
    /* Patch to allow customisation of the getFirstEntryY logic */
    @Inject(method = "getFirstEntryY", at = @At(value = "HEAD"), cancellable = true)
    private void getFirstItemY(CallbackInfoReturnable<Integer> cir)
    {
        if(((Object) this) instanceof FrameworkSelectionList list)
        {
            cir.setReturnValue(list.getFirstItemY());
        }
    }

    /* Patch to allow customisation of the repositionEntries logic */
    @Inject(method = "repositionEntries", at = @At(value = "HEAD"), cancellable = true)
    private void repositionItems(CallbackInfo ci)
    {
        if(((Object) this) instanceof FrameworkSelectionList list)
        {
            list.repositionItems();
            ci.cancel();
        }
    }
}
