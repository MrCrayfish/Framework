package com.mrcrayfish.framework.api.client.model;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelBaker;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;

public class NeoForgeModelResource<T> extends FrameworkModelResource<T>
{
    private final StandaloneModelKey<T> key;
    private final StandaloneModelBaker<T> baker;

    public NeoForgeModelResource(ResourceLocation location, FrameworkModelBaker<T> baker)
    {
        super(location, baker);
        this.key = new StandaloneModelKey<>(location);
        this.baker = (model, vanillaBaker) -> baker.bake(location, model, vanillaBaker);
    }

    public StandaloneModelKey<T> standaloneKey()
    {
        return this.key;
    }

    public StandaloneModelBaker<T> modelBaker()
    {
        return this.baker;
    }
}
