package com.mrcrayfish.framework.api.serialize;

/**
 * Author: MrCrayfish
 */
public final class DataNumber extends DataEntry
{
    private final Number value;

    DataNumber(Number value)
    {
        super(DataType.NUMBER);
        this.value = value;
    }

    /**
     * @return This data number as a byte
     */
    public byte asByte()
    {
        return this.value.byteValue();
    }

    /**
     * @return This data number as an integer
     */
    public int asInt()
    {
        return this.value.intValue();
    }

    /**
     * @return This data number as a float
     */
    public float asFloat()
    {
        return this.value.floatValue();
    }

    /**
     * @return This data number as a double
     */
    public double asDouble()
    {
        return this.value.doubleValue();
    }
}
