package com.mrcrayfish.framework.client;

import com.mrcrayfish.framework.Constants;
import com.mrcrayfish.framework.api.event.InputEvents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;

/**
 * Author: MrCrayfish
 */
@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientFrameworkNeoForge
{
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event)
    {
        ClientBootstrap.init();
        NeoForge.EVENT_BUS.register(new ClientNeoForgeEvents());
    }

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event)
    {
        InputEvents.REGISTER_KEY_MAPPING.post().handle(event::register);
    }

    @SubscribeEvent
    public static void registerReloadListener(RegisterClientReloadListenersEvent event)
    {
        event.registerReloadListener(JsonDataManager.getInstance());
    }
}
