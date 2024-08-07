package com.mrcrayfish.framework.api.serialize;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;

import java.util.function.Consumer;

/**
 * Author: MrCrayfish
 */
public final class DataArray extends DataEntry
{
    private final ImmutableList<DataEntry> values;

    DataArray(JsonArray array)
    {
        super(DataType.ARRAY);
        this.values = construct(array);
    }

    /**
     * @return The length of this data array
     */
    public int length()
    {
        return this.values.size();
    }

    /**
     * Performs an action on each data entry in this array. The order which this is iterated is
     * determined by the initial Json Object which was used to create this Data Array, which is the
     * order that is defined in the raw json file.
     *
     * @param consumer the action to perform on each data entry
     */
    public void forEach(Consumer<DataEntry> consumer)
    {
        this.values.forEach(consumer);
    }

    /**
     * Gets the data entry at the given index. An exception will be thrown if the index is out
     * of bounds. Use {@link #length()} to determine the upper bounds.
     *
     * @param index the index of the entry
     * @return a data entry instance
     */
    public DataEntry get(int index)
    {
        return this.values.get(index);
    }

    /**
     * @return An immutable list of all the values in this data array.
     */
    public ImmutableList<DataEntry> values()
    {
        return this.values;
    }

    private static ImmutableList<DataEntry> construct(JsonArray array)
    {
        ImmutableList.Builder<DataEntry> builder = new ImmutableList.Builder<>();
        array.forEach(element ->
        {
            DataEntry entry = convertElement(element);
            if(entry != null)
            {
                builder.add(entry);
            }
        });
        return builder.build();
    }
}
