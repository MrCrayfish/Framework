package com.mrcrayfish.framework.api.client.model;

import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;
import net.neoforged.neoforge.client.model.standalone.UnbakedStandaloneModel;

public class NeoForgeModelResource<T> extends FrameworkModelResource<T>
{
    private final StandaloneModelKey<T> key;
    private final UnbakedStandaloneModel<T> baker;

    public NeoForgeModelResource(ResourceLocation location, FrameworkModelBaker<T> modelBaker)
    {
        super(location, modelBaker);
        this.key = new StandaloneModelKey<>(location::toString);
        this.baker = new UnbakedStandaloneModel<>()
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

    public StandaloneModelKey<T> standaloneKey()
    {
        return this.key;
    }

    public UnbakedStandaloneModel<T> unbakedModel()
    {
        return this.baker;
    }
}
