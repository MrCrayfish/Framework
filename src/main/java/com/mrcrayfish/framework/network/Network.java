package com.mrcrayfish.framework.network;

import com.mrcrayfish.framework.Reference;
import com.mrcrayfish.framework.api.network.ChannelBuilder;
import com.mrcrayfish.framework.network.message.handshake.S2CSyncedPlayerData;
import com.mrcrayfish.framework.network.message.play.S2CUpdatePlayerData;
import net.minecraftforge.fmllegacy.network.NetworkDirection;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;

/**
 * Author: MrCrayfish
 */
public class Network
{
    private static final SimpleChannel HANDSHAKE_CHANNEL = ChannelBuilder
            .create(Reference.MOD_ID, "handshake", 1)
            .registerHandshakeMessage(S2CSyncedPlayerData.class)
            .build();

    private static final SimpleChannel PLAY_CHANNEL = ChannelBuilder
            .create(Reference.MOD_ID, "play", 1)
            .registerPlayMessage(S2CUpdatePlayerData.class, NetworkDirection.PLAY_TO_CLIENT)
            .build();

    public static void init() {}

    public static SimpleChannel getHandshakeChannel()
    {
        return HANDSHAKE_CHANNEL;
    }

    public static SimpleChannel getPlayChannel()
    {
         return PLAY_CHANNEL;
    }
}
