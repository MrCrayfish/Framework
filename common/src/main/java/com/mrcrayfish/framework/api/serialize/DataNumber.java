package com.mrcrayfish.framework.api.serialize;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.LazilyParsedNumber;

/**
 * Author: MrCrayfish
 */
public final class DataNumber extends DataEntry
{
    private final Number value;

    DataNumber(Number value)
    {
        super(DataType.NUMBER);
        // Optimise value since LazilyParsedNumber will parse every call
        if(value instanceof LazilyParsedNumber parsed)
        {
            if(parsed.toString().contains("."))
            {
                this.value = parsed.doubleValue();
            }
            else
            {
                this.value = parsed.longValue();
            }
        }
        else
        {
            this.value = value;
        }
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
     * @return This data number as a long
     */
    public long asLong()
    {
        return this.value.longValue();
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

    @Override
    protected JsonElement toJson()
    {
        return new JsonPrimitive(this.value);
    }
}
