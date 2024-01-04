package com.mrcrayfish.framework.api.serialize;

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
}
