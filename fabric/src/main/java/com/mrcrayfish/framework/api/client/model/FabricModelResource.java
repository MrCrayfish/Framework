package com.mrcrayfish.framework.api.client.model;

import net.minecraft.resources.ResourceLocation;

public class FabricModelResource<T> extends FrameworkModelResource<T>
{
    public FabricModelResource(ResourceLocation location, FrameworkModelBaker<T> baker)
    {
        super(location, baker);
    }
}
