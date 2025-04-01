package com.mrcrayfish.framework.api.client.model;

import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvedModel;

public interface FrameworkModelBaker<T>
{
    T bake(ResolvedModel model, ModelBaker baker);
}
