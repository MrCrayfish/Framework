package com.mrcrayfish.framework.api.serialize;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

/**
 * Author: MrCrayfish
 */
public final class DataBoolean extends DataEntry
{
    static final DataBoolean TRUE = new DataBoolean(true);
    static final DataBoolean FALSE = new DataBoolean(false);

    private final boolean value;

    private DataBoolean(boolean value)
    {
        super(DataType.BOOLEAN);
        this.value = value;
    }

    /**
     * @return This data as a regular java boolean
     */
    public boolean asBoolean()
    {
        return this.value;
    }

    @Override
    protected JsonElement toJson()
    {
        return new JsonPrimitive(this.value);
    }
}
