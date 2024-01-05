package com.mrcrayfish.framework.mixin;

import com.mrcrayfish.framework.api.event.PlayerEvents;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Author: MrCrayfish
 */
@Mixin(PlayerList.class)
public class PlayerListMixin
{
    @Inject(method = "placeNewPlayer", at = @At(value = "TAIL"))
    private void frameworkOnPlayerJoin(Connection connection, ServerPlayer player, CommonListenerCookie cookie, CallbackInfo ci)
    {
        PlayerEvents.LOGGED_IN.post().handle(player);
    }

    @Inject(method = "remove", at = @At(value = "HEAD"))
    private void frameworkOnPlayerLeave(ServerPlayer player, CallbackInfo ci)
    {
        PlayerEvents.LOGGED_OUT.post().handle(player);
    }
}
