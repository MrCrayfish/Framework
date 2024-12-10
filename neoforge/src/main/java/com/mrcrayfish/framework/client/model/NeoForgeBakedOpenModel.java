package com.mrcrayfish.framework.client.model;

import com.mrcrayfish.framework.api.serialize.DataObject;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.DelegateBakedModel;
import org.jetbrains.annotations.Nullable;

/**
 * Author: MrCrayfish
 */
public class NeoForgeBakedOpenModel extends DelegateBakedModel implements IOpenModel
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
