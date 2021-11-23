package com.mrcrayfish.framework;

import com.mrcrayfish.framework.api.data.SyncedPlayerData;
import com.mrcrayfish.framework.network.Network;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Author: MrCrayfish
 */
@Mod(Reference.MOD_ID)
public class Framework
{
    public static final Logger LOGGER = LogManager.getLogger(Reference.MOD_ID);

    public Framework()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::onCommonSetup);
        bus.addListener(this::onRegisterCapabilities);
        MinecraftForge.EVENT_BUS.register(SyncedPlayerData.instance());
    }

    private void onCommonSetup(FMLCommonSetupEvent event)
    {
        Network.init();
    }

    public void onRegisterCapabilities(RegisterCapabilitiesEvent event)
    {
        SyncedPlayerData.onRegisterCapability(event);
    }
}
