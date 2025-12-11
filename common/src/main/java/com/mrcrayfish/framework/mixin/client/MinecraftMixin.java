package com.mrcrayfish.framework.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.mrcrayfish.framework.config.FrameworkConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin
{
    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screens/Screen;ZZ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;dropAllTasks()V"))
    private void unloadConfigs(Screen screen, boolean transferring, boolean unknown, CallbackInfo ci, @Local(ordinal = 0) ClientPacketListener listener)
    {
        Connection connection = listener.getConnection();
        FrameworkConfigManager.getInstance().onClientDisconnect(connection);
    }
}
