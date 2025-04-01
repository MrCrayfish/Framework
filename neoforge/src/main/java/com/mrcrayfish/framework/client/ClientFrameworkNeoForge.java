package com.mrcrayfish.framework.client;

import com.mrcrayfish.framework.Constants;
import com.mrcrayfish.framework.api.client.model.NeoForgeModelResource;
import com.mrcrayfish.framework.api.event.ClientEvents;
import com.mrcrayfish.framework.api.event.InputEvents;
import com.mrcrayfish.framework.client.model.FrameworkBlockStateModel;
import com.mrcrayfish.framework.client.model.FrameworkItemModel;
import com.mrcrayfish.framework.client.model.NeoForgeFrameworkBlockStateModel;
import com.mrcrayfish.framework.client.model.StandaloneModelManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelBaker;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;
import net.neoforged.neoforge.common.NeoForge;

import java.util.function.BiConsumer;

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
    private static void registerReloadListener(AddClientReloadListenersEvent event)
    {
        event.addListener(JsonDataManager.ID, JsonDataManager.getInstance());
    }

    @SubscribeEvent
    private static void onRegisterAdditionalModels(ModelEvent.RegisterStandalone event)
    {
        StandaloneModelManager.getInstance().load(resource -> {
            resource.clearCache(); // Make sure models are reset
            registerStandaloneModel(event::register, (NeoForgeModelResource<?>) resource);
        });
    }

    // Fighting generics one method at a time
    private static <T> void registerStandaloneModel(BiConsumer<StandaloneModelKey<T>, StandaloneModelBaker<T>> consumer, NeoForgeModelResource<T> key)
    {
        consumer.accept(key.standaloneKey(), key.modelBaker());
    }

    @SubscribeEvent
    private static void onRegisterItemModels(RegisterItemModelsEvent event)
    {
        event.register(FrameworkItemModel.ID, FrameworkItemModel.Unbaked.MAP_CODEC);
    }

    @SubscribeEvent
    private static void onRegisterItemModels(RegisterBlockStateModels event)
    {
        event.registerModel(FrameworkBlockStateModel.ID, NeoForgeFrameworkBlockStateModel.Unbaked.MAP_CODEC);
    }
}
