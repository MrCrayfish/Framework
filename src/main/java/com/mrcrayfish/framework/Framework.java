package com.mrcrayfish.framework;

import com.mrcrayfish.framework.api.event.FrameworkEvent;
import com.mrcrayfish.framework.common.data.SyncedEntityData;
import com.mrcrayfish.framework.network.Network;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Author: MrCrayfish
 */
@Mod(Reference.MOD_ID)
public class Framework
{
    public static final Logger LOGGER = LogManager.getLogger("Framework");

    private static boolean gameLoaded = false;

    public Framework()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::onCommonSetup);
        bus.addListener(this::onLoadComplete);
        bus.addListener(SyncedEntityData::registerCapability);
        MinecraftForge.EVENT_BUS.register(SyncedEntityData.instance());
    }

    private void onCommonSetup(FMLCommonSetupEvent event)
    {
        event.enqueueWork(() -> {
            Network.init();
            ModLoader.get().postEvent(new FrameworkEvent.Register());
        });
    }

    private void onLoadComplete(FMLLoadCompleteEvent event)
    {
        gameLoaded = true;
    }

    public static boolean isGameLoaded()
    {
        return gameLoaded;
    }
}
