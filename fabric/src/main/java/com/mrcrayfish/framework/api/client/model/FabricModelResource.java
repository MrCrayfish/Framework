package com.mrcrayfish.framework.api.client.model;

import net.fabricmc.fabric.api.client.model.loading.v1.ExtraModelKey;
import net.fabricmc.fabric.api.client.model.loading.v1.UnbakedExtraModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.resources.ResourceLocation;

public class FabricModelResource<T> extends FrameworkModelResource<T>
{
    private final ExtraModelKey<T> extraModelKey;
    private final UnbakedExtraModel<T> unbakedModel;

    public FabricModelResource(ResourceLocation location, FrameworkModelBaker<T> modelBaker)
    {
        super(location, modelBaker);
        this.extraModelKey = ExtraModelKey.create(location::toString);
        this.unbakedModel = new UnbakedExtraModel<>()
        {
            @Override
            public T bake(ModelBaker baker)
            {
                ResolvedModel resolvedModel = baker.getModel(location);
                return modelBaker.bake(resolvedModel, baker);
            }

            @Override
            public void resolveDependencies(Resolver resolver)
            {
                resolver.markDependency(location);
            }
        };
    }

    public ExtraModelKey<T> extraModelKey()
    {
        return this.extraModelKey;
    }

    public UnbakedExtraModel<T> unbakedModel()
    {
        return this.unbakedModel;
    }
}
