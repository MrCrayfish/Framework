package com.mrcrayfish.framework.client.model;

import com.mrcrayfish.framework.api.serialize.DataObject;

/**
 * Author: MrCrayfish
 */
public interface IOpenModel
{
    DataObject getData();

    static DataObject getData(Object obj)
    {
        return obj instanceof IOpenModel model ? model.getData() : DataObject.EMPTY;
    }
}
