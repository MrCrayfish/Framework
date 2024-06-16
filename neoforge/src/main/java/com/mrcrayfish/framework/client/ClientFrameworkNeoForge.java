package com.mrcrayfish.framework.client;

import com.mrcrayfish.framework.Constants;
import com.mrcrayfish.framework.api.event.InputEvents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;

/**
 * Author: MrCrayfish
 */
@EventBusSubscriber(modid = Constants.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientFrameworkNeoForge
{
    @SubscribeEvent
    private static void onClientSetup(FMLClientSetupEvent event)
    {
        ClientBootstrap.init();
        NeoForge.EVENT_BUS.register(new ClientNeoForgeEvents());
    }

    @SubscribeEvent
    private static void registerKeyMappings(RegisterKeyMappingsEvent event)
    {
        InputEvents.REGISTER_KEY_MAPPING.post().handle(event::register);
    }

    @SubscribeEvent
    private static void registerReloadListener(RegisterClientReloadListenersEvent event)
    {
        event.registerReloadListener(JsonDataManager.getInstance());
    }

    @SubscribeEvent
    private static void onRegisterAdditionalModels(ModelEvent.RegisterAdditional event)
    {
        StandaloneModelManager.getInstance().load(event::register);
    }
}
