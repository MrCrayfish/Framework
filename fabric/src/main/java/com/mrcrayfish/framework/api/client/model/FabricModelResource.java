package com.mrcrayfish.framework.api.client.model;

import net.minecraft.resources.Identifier;

public class FabricModelResource<T> extends FrameworkModelResource<T>
{
    //private final ExtraModelKey<T> extraModelKey;
    //private final UnbakedExtraModel<T> unbakedModel;

    public FabricModelResource(Identifier location, FrameworkModelBaker<T> modelBaker)
    {
        super(location, modelBaker);
        /*this.extraModelKey = ExtraModelKey.create(location::toString);
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
        };*/
    }

    /*public ExtraModelKey<T> extraModelKey()
    {
        return this.extraModelKey;
    }

    public UnbakedExtraModel<T> unbakedModel()
    {
        return this.unbakedModel;
    }*/
}
