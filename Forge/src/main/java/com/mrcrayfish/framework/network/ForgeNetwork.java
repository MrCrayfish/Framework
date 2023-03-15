package com.mrcrayfish.framework.network;

import com.mrcrayfish.framework.Constants;
import com.mrcrayfish.framework.api.FrameworkAPI;
import com.mrcrayfish.framework.api.network.FrameworkNetwork;
import com.mrcrayfish.framework.network.message.handshake.S2CSyncedEntityData;
import com.mrcrayfish.framework.network.message.play.S2CUpdateEntityData;
import net.minecraft.resources.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public class ForgeNetwork
{
    private static final FrameworkNetwork HANDSHAKE_CHANNEL = FrameworkAPI
            .createNetworkBuilder(new ResourceLocation(Constants.MOD_ID, "forge_handshake"), 1)
            .registerHandshakeMessage(S2CSyncedEntityData.class, true)
            .build();

    private static final FrameworkNetwork PLAY_CHANNEL = FrameworkAPI
            .createNetworkBuilder(new ResourceLocation(Constants.MOD_ID, "forge_play"), 1)
            .registerPlayMessage(S2CUpdateEntityData.class)
            .build();

    public static void init() {}

    public static FrameworkNetwork getPlayChannel()
    {
        return PLAY_CHANNEL;
    }
}
