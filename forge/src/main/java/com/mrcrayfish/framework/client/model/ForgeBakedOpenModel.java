package com.mrcrayfish.framework.client.model;

import com.mrcrayfish.framework.api.serialize.DataObject;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraftforge.client.model.BakedModelWrapper;

import org.jetbrains.annotations.Nullable;

/**
 * Author: MrCrayfish
 */
public class ForgeBakedOpenModel extends BakedModelWrapper<BakedModel> implements IOpenModel
{
    private final DataObject data;

    public ForgeBakedOpenModel(BakedModel originalModel, @Nullable DataObject data)
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
