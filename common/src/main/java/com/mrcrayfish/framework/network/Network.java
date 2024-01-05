package com.mrcrayfish.framework.network;

import com.mrcrayfish.framework.Constants;
import com.mrcrayfish.framework.api.FrameworkAPI;
import com.mrcrayfish.framework.api.network.FrameworkNetwork;
import com.mrcrayfish.framework.api.network.MessageDirection;
import com.mrcrayfish.framework.config.FrameworkConfigManager;
import com.mrcrayfish.framework.network.message.configuration.S2CLoginData;
import com.mrcrayfish.framework.network.message.configuration.S2CConfigData;
import com.mrcrayfish.framework.network.message.configuration.S2CSyncedEntityData;
import com.mrcrayfish.framework.network.message.play.S2CSyncConfigData;
import com.mrcrayfish.framework.network.message.play.S2CUpdateEntityData;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public class Network
{
    private static FrameworkNetwork configurationChannel;

    private static FrameworkNetwork playChannel;

    public static void init()
    {
        configurationChannel = FrameworkAPI.createNetworkBuilder(new ResourceLocation(Constants.MOD_ID, "configuration"), 1)
            .registerConfigurationMessage(S2CLoginData.class, "login_data", LoginDataManager::getConfigurationMessages)
            .registerConfigurationMessage(S2CConfigData.class, "config_data", FrameworkConfigManager.getInstance()::getConfigurationMessages)
            .registerConfigurationMessage(S2CSyncedEntityData.class, "synced_entity_data", () -> List.of(new S2CSyncedEntityData()))
            .build();
        playChannel = FrameworkAPI.createNetworkBuilder(new ResourceLocation(Constants.MOD_ID, "play"), 1)
            .registerPlayMessage(S2CUpdateEntityData.class, MessageDirection.PLAY_CLIENT_BOUND)
            .registerPlayMessage(S2CSyncConfigData.class, MessageDirection.PLAY_CLIENT_BOUND)
            .ignoreServer()
            .build();
    }

    public static FrameworkNetwork getConfigurationChannel()
    {
        return configurationChannel;
    }

    public static FrameworkNetwork getPlayChannel()
    {
        return playChannel;
    }
}
