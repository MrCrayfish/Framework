package com.mrcrayfish.framework.mixin.client;

import com.mrcrayfish.framework.config.FrameworkConfigManager;
import net.minecraft.client.multiplayer.ClientConfigurationPacketListenerImpl;
import net.minecraft.network.protocol.configuration.ClientboundFinishConfigurationPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConfigurationPacketListenerImpl.class)
public class ClientConfigurationPacketListenerImplMixin
{
    @Inject(method = "handleConfigurationFinished", at = @At(value = "RETURN"))
    private void loadDefaultConfigs(ClientboundFinishConfigurationPacket packet, CallbackInfo ci)
    {
        // Framework configs should be synced at this point, however if the server is non-modded,
        // load the defaults for synced configs.
        FrameworkConfigManager.getInstance().loadDefaultSyncConfigsIfUnloaded();
    }
}
