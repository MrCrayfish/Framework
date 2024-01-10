package com.mrcrayfish.framework.network;

import com.mrcrayfish.framework.Constants;
import com.mrcrayfish.framework.api.FrameworkAPI;
import com.mrcrayfish.framework.api.network.FrameworkNetwork;
import com.mrcrayfish.framework.config.FrameworkConfigManager;
import com.mrcrayfish.framework.entity.sync.SyncedEntityData;
import com.mrcrayfish.framework.network.message.configuration.S2CConfigData;
import com.mrcrayfish.framework.network.message.configuration.S2CLoginData;
import com.mrcrayfish.framework.network.message.configuration.S2CSyncedEntityData;
import com.mrcrayfish.framework.network.message.play.S2CSyncConfigData;
import com.mrcrayfish.framework.network.message.play.S2CUpdateEntityData;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;

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
            .registerConfigurationMessage("login_data", S2CLoginData.class, S2CLoginData::encode, S2CLoginData::decode, S2CLoginData::handle, LoginDataManager::getConfigurationMessages)
            .registerConfigurationMessage("config_data", S2CConfigData.class, S2CConfigData::encode, S2CConfigData::decode, S2CConfigData::handle, () -> FrameworkConfigManager.getInstance().getConfigurationMessages())
            .registerConfigurationMessage("synced_entity_data", S2CSyncedEntityData.class, S2CSyncedEntityData::encode, S2CSyncedEntityData::decode, S2CSyncedEntityData::handle, () -> SyncedEntityData.instance().getConfigurationMessages())
            .optional()
            .build();
        playChannel = FrameworkAPI.createNetworkBuilder(new ResourceLocation(Constants.MOD_ID, "play"), 1)
            .registerPlayMessage("update_entity_data", S2CUpdateEntityData.class, S2CUpdateEntityData::encode, S2CUpdateEntityData::decode, S2CUpdateEntityData::handle, PacketFlow.CLIENTBOUND)
            .registerPlayMessage("sync_config_data", S2CSyncConfigData.class, S2CSyncConfigData::encode, S2CSyncConfigData::decode, S2CSyncConfigData::handle, PacketFlow.CLIENTBOUND)
            .optional()
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
