package com.mrcrayfish.framework.client.model;

import com.mrcrayfish.framework.api.serialize.DataObject;
import net.minecraft.client.resources.model.BakedModel;
import net.neoforged.neoforge.client.model.BakedModelWrapper;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class NeoForgeBakedOpenModel extends BakedModelWrapper<BakedModel> implements IOpenModel
{
    private final DataObject data;

    public NeoForgeBakedOpenModel(BakedModel originalModel, @Nullable DataObject data)
    {
        super(originalModel);
        this.data = data;
    }

    @Override
    public DataObject getData()
    {
        return this.data;
    }
}
