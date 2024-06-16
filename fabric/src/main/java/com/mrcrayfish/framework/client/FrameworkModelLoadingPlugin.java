package com.mrcrayfish.framework.client;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;

/**
 * Author: MrCrayfish
 */
public class FrameworkModelLoadingPlugin implements ModelLoadingPlugin
{
    @Override
    public void onInitializeModelLoader(Context context)
    {
        StandaloneModelManager.getInstance().load(location -> {
            context.addModels(location.id());
        });
    }
}
