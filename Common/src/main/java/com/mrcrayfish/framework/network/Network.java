package com.mrcrayfish.framework.network;

import com.mrcrayfish.framework.Constants;
import com.mrcrayfish.framework.api.FrameworkAPI;
import com.mrcrayfish.framework.api.network.FrameworkNetwork;
import com.mrcrayfish.framework.network.message.handshake.S2CLoginData;
import net.minecraft.resources.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public class Network
{
    private static final FrameworkNetwork HANDSHAKE_CHANNEL = FrameworkAPI
            .createNetworkBuilder(new ResourceLocation(Constants.MOD_ID, "handshake"), 1)
            .registerHandshakeMessage(S2CLoginData.class, LoginDataManager::getLoginDataMessages)
            .build();

    public static void init() {}

    public static FrameworkNetwork getHandshakeChannel()
    {
        return HANDSHAKE_CHANNEL;
    }


}
