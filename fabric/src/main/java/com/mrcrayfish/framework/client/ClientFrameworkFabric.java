package com.mrcrayfish.framework.client;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mrcrayfish.framework.FrameworkData;
import com.mrcrayfish.framework.api.LogicalEnvironment;
import com.mrcrayfish.framework.api.util.TaskRunner;
import com.mrcrayfish.framework.client.model.FrameworkItemModel;
import com.mrcrayfish.framework.config.ConfigWatcher;
import com.mrcrayfish.framework.platform.Services;
import com.mrcrayfish.framework.util.Utils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.item.ItemModels;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Author: MrCrayfish
 */
public class ClientFrameworkFabric implements ClientModInitializer
{
    public static final ResourceLocation OPEN_MODEL_ID = Utils.rl("open_model");

    @Override
    public void onInitializeClient()
    {
        TaskRunner.setExecutor(LogicalEnvironment.CLIENT, Minecraft.getInstance());
        ItemModels.ID_MAPPER.put(FrameworkItemModel.ID, FrameworkItemModel.Unbaked.MAP_CODEC);
        ModelLoadingPlugin.register(new FrameworkModelLoadingPlugin());
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new IdentifiableResourceReloadListener() {
            @Override
            public ResourceLocation getFabricId() {
                return Utils.rl("json_data_manager");
            }
            @Override
            public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, Executor executor, Executor executor2) {
                return JsonDataManager.getInstance().reload(preparationBarrier, resourceManager, executor, executor2);
            }
        });
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            FrameworkData.setLoaded();
        });
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            ConfigWatcher.get().stop();
        });
        Services.REGISTRATION.getRegistryObjects(RenderPipeline.class).forEach(RenderPipelines::register);
    }
}
