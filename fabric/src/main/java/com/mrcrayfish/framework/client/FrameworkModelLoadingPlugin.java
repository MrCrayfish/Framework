package com.mrcrayfish.framework.client;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;

/**
 * Author: MrCrayfish
 */
public class FrameworkModelLoadingPlugin implements ModelLoadingPlugin
{
    @Override
    public void initialize(Context context)
    {
        // TODO not possible in Fabric yet.
        //StandaloneModelManager.getInstance().load(context::addModels);
    }
}
