package com.mrcrayfish.framework.client;

import com.mrcrayfish.framework.Constants;
import com.mrcrayfish.framework.GameStates;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Author: MrCrayfish
 */
public class FrameworkClientFabric implements ClientModInitializer
{
    public static final ResourceLocation OPEN_MODEL_ID = new ResourceLocation(Constants.MOD_ID, "open_model");

    @Override
    public void onInitializeClient()
    {
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new IdentifiableResourceReloadListener()
        {
            @Override
            public ResourceLocation getFabricId()
            {
                return new ResourceLocation(Constants.MOD_ID, "json_data_manager");
            }

            @Override
            public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller profilerFiller, ProfilerFiller profilerFiller2, Executor executor, Executor executor2)
            {
                return JsonDataManager.getInstance().reload(preparationBarrier, resourceManager, profilerFiller, profilerFiller2, executor, executor2);
            }
        });

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            GameStates.setLoaded();
        });
    }
}
