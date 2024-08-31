package com.mrcrayfish.framework.client.model;

import com.mrcrayfish.framework.api.serialize.DataObject;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.minecraft.client.resources.model.BakedModel;
import org.jetbrains.annotations.Nullable;

/**
 * Author: MrCrayfish
 */
public class FabricBakedOpenModel extends ForwardingBakedModel implements IOpenModel
{
    private final DataObject data;

    public FabricBakedOpenModel(BakedModel originalModel, @Nullable DataObject data)
    {
        this.wrapped = originalModel;
        this.data = data;
    }

    @Override
    public DataObject getData()
    {
        return this.data;
    }
}
