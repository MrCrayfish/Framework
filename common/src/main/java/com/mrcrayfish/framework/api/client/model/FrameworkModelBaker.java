package com.mrcrayfish.framework.api.client.model;

import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.resources.ResourceLocation;

public interface FrameworkModelBaker<T>
{
    T bake(ResourceLocation location, ResolvedModel model, ModelBaker baker);
}
