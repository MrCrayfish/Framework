package com.mrcrayfish.framework.client;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mrcrayfish.framework.FrameworkData;
import com.mrcrayfish.framework.api.LogicalEnvironment;
import com.mrcrayfish.framework.api.util.TaskRunner;
//import com.mrcrayfish.framework.client.model.FabricFrameworkBlockStateModel;
//import com.mrcrayfish.framework.client.model.FrameworkBlockStateModel;
import com.mrcrayfish.framework.client.model.FrameworkItemModel;
//import com.mrcrayfish.framework.client.model.geometry.OpenModelGeometry;
import com.mrcrayfish.framework.config.ConfigWatcher;
import com.mrcrayfish.framework.platform.Services;
import com.mrcrayfish.framework.util.Utils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
//import net.fabricmc.fabric.api.client.model.loading.v1.CustomUnbakedBlockStateModel;
//import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
//import net.fabricmc.fabric.api.client.model.loading.v1.UnbakedModelDeserializer;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.item.ItemModels;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;

/**
 * Author: MrCrayfish
 */
public class ClientFrameworkFabric implements ClientModInitializer
{
    public static final Identifier OPEN_MODEL_ID = Utils.rl("open_model");

    @Override
    public void onInitializeClient()
    {
        TaskRunner.setExecutor(LogicalEnvironment.CLIENT, Minecraft.getInstance());
        ItemModels.ID_MAPPER.put(FrameworkItemModel.ID, FrameworkItemModel.Unbaked.MAP_CODEC);
        //CustomUnbakedBlockStateModel.register(FrameworkBlockStateModel.ID, FabricFrameworkBlockStateModel.Unbaked.MAP_CODEC);
        //UnbakedModelDeserializer.register(OpenModelGeometry.Loader.ID, new OpenModelGeometry.Loader());
        //ModelLoadingPlugin.register(new FrameworkModelLoadingPlugin());
        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloader(Utils.rl("json_data_manager"), JsonDataManager.getInstance());
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            FrameworkData.setLoaded();
        });
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            ConfigWatcher.get().stop();
        });
        Services.REGISTRATION.getRegistryObjects(RenderPipeline.class).forEach(RenderPipelines::register);
    }
}
