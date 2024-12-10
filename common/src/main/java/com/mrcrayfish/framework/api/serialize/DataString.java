package com.mrcrayfish.framework.api.serialize;

import com.google.gson.JsonPrimitive;

/**
 * Author: MrCrayfish
 */
public final class DataString extends DataEntry
{
    private final String value;

    DataString(String value)
    {
        super(DataType.STRING);
        this.value = value;
    }

    /**
     * @return This data as a regular java String
     */
    public String asString()
    {
        return this.value;
    }

    @Override
    protected JsonPrimitive toJson()
    {
        return new JsonPrimitive(this.value);
    }
}
