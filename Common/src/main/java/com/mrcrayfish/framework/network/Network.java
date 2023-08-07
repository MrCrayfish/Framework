package com.mrcrayfish.framework.network;

import com.mrcrayfish.framework.Constants;
import com.mrcrayfish.framework.api.FrameworkAPI;
import com.mrcrayfish.framework.api.network.FrameworkNetwork;
import com.mrcrayfish.framework.api.network.MessageDirection;
import com.mrcrayfish.framework.config.FrameworkConfigManager;
import com.mrcrayfish.framework.network.message.handshake.S2CLoginData;
import com.mrcrayfish.framework.network.message.handshake.S2CLoginConfigData;
import com.mrcrayfish.framework.network.message.play.S2CSyncConfigData;
import com.mrcrayfish.framework.network.message.play.S2CUpdateEntityData;
import net.minecraft.resources.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public class Network
{
    private static final FrameworkNetwork HANDSHAKE_CHANNEL = FrameworkAPI
            .createNetworkBuilder(new ResourceLocation(Constants.MOD_ID, "handshake"), 1)
            .registerHandshakeMessage(S2CLoginData.class, LoginDataManager::getLoginDataMessages)
            .registerHandshakeMessage(S2CLoginConfigData.class, FrameworkConfigManager.getInstance()::getMessagesForLogin)
            .ignoreServer()
            .build();

    private static final FrameworkNetwork PLAY_CHANNEL = FrameworkAPI
            .createNetworkBuilder(new ResourceLocation(Constants.MOD_ID, "play"), 1)
            .registerPlayMessage(S2CUpdateEntityData.class, MessageDirection.PLAY_CLIENT_BOUND)
            .registerPlayMessage(S2CSyncConfigData.class, MessageDirection.PLAY_CLIENT_BOUND)
            .ignoreServer()
            .build();

    public static void init() {}

    public static FrameworkNetwork getHandshakeChannel()
    {
        return HANDSHAKE_CHANNEL;
    }

    public static FrameworkNetwork getPlayChannel()
    {
        return PLAY_CHANNEL;
    }
}
