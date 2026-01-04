package com.mrcrayfish.framework.mixin.client;

import com.mrcrayfish.framework.api.client.screen.widget.FrameworkEditBox;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EditBox.class)
public class EditBoxMixin
{
    @Shadow
    private int textY;

    @Shadow
    @Final
    private Font font;

    /* Forces FrameworkEditBox text position to always be centered
     * vertically even if bordered is set to false. */
    @Inject(method = "updateTextPosition", at = @At(value = "TAIL"))
    private void alwaysCenterVertically(CallbackInfo ci)
    {
        EditBox box = (EditBox) (Object) this;
        if(box instanceof FrameworkEditBox.Impl && this.font != null)
        {
            this.textY = box.getY() + (box.getHeight() - 8) / 2;
        }
    }
}
