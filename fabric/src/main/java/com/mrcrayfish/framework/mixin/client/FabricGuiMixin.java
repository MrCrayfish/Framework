package com.mrcrayfish.framework.mixin.client;

import com.mrcrayfish.framework.api.client.screen.overlay.OverlayController;
import com.mrcrayfish.framework.api.client.screen.overlay.Overlayable;
import com.mrcrayfish.framework.api.event.client.FrameworkScreenEvents;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * Author: MrCrayfish
 */
@Mixin(Gui.class)
public class FabricGuiMixin
{
    @Inject(method = "setScreen", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/Gui;screen:Lnet/minecraft/client/gui/screens/Screen;", opcode = Opcodes.PUTFIELD), locals = LocalCapture.CAPTURE_FAILHARD)
    private void frameworkOnScreenAdded(Screen screen, CallbackInfo ci)
    {
        FrameworkScreenEvents.OPENED.post().handle(screen);
    }

    @Inject(method = "setScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;removed()V"))
    private void frameworkOnScreenClosed(Screen screen, CallbackInfo ci)
    {
        Gui gui = (Gui) (Object) this;
        FrameworkScreenEvents.CLOSED.post().handle(gui.screen());

        if(screen instanceof Overlayable overlayable)
        {
            OverlayController controller = overlayable.getOverlayController();
            controller.closeAll();
        }
    }
}
