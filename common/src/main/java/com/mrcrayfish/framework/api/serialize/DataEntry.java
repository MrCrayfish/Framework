package com.mrcrayfish.framework.api.serialize;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Author: MrCrayfish
 */
public abstract sealed class DataEntry permits DataObject, DataArray, DataString, DataNumber, DataBoolean
{
    private UUID id;
    private final DataType type;

    protected DataEntry(DataType type)
    {
        this.type = type;
    }

    /**
     * @return A unique identifier for this entry. Lazily initialized
     */
    public UUID getId()
    {
        if(this.id == null)
        {
            this.id = UUID.randomUUID();
        }
        return this.id;
    }

    /**
     * @return The data type of this entry
     */
    public final DataType getType()
    {
        return this.type;
    }

    @Nullable
    static DataEntry convertElement(JsonElement element)
    {
        if(element.isJsonObject())
        {
            return new DataObject(element.getAsJsonObject());
        }
        else if(element.isJsonArray())
        {
            return new DataArray(element.getAsJsonArray());
        }
        else if(element.isJsonPrimitive())
        {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if(primitive.isString())
            {
                return new DataString(primitive.getAsString());
            }
            else if(primitive.isNumber())
            {
                return new DataNumber(primitive.getAsNumber());
            }
            else if(primitive.isBoolean())
            {
                return primitive.getAsBoolean() ? DataBoolean.TRUE : DataBoolean.FALSE;
            }
        }
        return null;
    }
}
