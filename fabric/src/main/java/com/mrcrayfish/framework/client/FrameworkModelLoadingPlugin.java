package com.mrcrayfish.framework.client;

import com.mrcrayfish.framework.api.client.model.FabricModelResource;
import com.mrcrayfish.framework.client.model.StandaloneModelManager;
import net.fabricmc.fabric.api.client.model.loading.v1.ExtraModelKey;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.UnbakedExtraModel;
//import net.fabricmc.fabric.api.client.model.loading.v1.ExtraModelKey;
//import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
//import net.fabricmc.fabric.api.client.model.loading.v1.UnbakedExtraModel;

import java.util.function.BiConsumer;

/**
 * Author: MrCrayfish
 */
public class FrameworkModelLoadingPlugin implements ModelLoadingPlugin
{
    @Override
    public void initialize(Context context)
    {
        StandaloneModelManager.getInstance().load(resource -> {
            resource.clearCache(); // Make sure models are reset
            registerStandaloneModel(context::addModel, (FabricModelResource<?>) resource);
        });
    }

    // Fighting generics one method at a time
    private static <T> void registerStandaloneModel(BiConsumer<ExtraModelKey<T>, UnbakedExtraModel<T>> consumer, FabricModelResource<T> key)
    {
        consumer.accept(key.extraModelKey(), key.unbakedModel());
    }
}
