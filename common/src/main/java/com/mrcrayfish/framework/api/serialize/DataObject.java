package com.mrcrayfish.framework.api.serialize;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * Author: MrCrayfish
 */
public final class DataObject extends DataEntry
{
    public static final DataObject EMPTY = new DataObject(ImmutableMap.of());

    private final ImmutableMap<String, DataEntry> children;

    DataObject(JsonObject object)
    {
        this(construct(object));
    }

    private DataObject(ImmutableMap<String, DataEntry> children)
    {
        super(DataType.OBJECT);
        this.children = children;
    }

    /**
     * @return True if this object has no data entries
     */
    public boolean isEmpty()
    {
        return this.children.isEmpty();
    }

    /**
     * @return An immutable set of all the entry keys
     */
    public Set<String> keys()
    {
        return this.children.keySet();
    }

    /**
     * @return An immutable collection of all the entries
     */
    public Collection<DataEntry> values()
    {
        return this.children.values();
    }

    /**
     * Performs an action on each data entry. The order which this is iterated is determined by the
     * initial Json Object which was used to create this Data Object, which is the order that is
     * defined in the raw json file.
     *
     * @param consumer the action to perform on each entry
     */
    public void forEach(BiConsumer<String, DataEntry> consumer)
    {
        this.children.forEach(consumer);
    }

    /**
     * Checks if this data object has a child entry matching the given key and type. This should be
     * called before retrieving the child data entry to avoid a cast exception.
     *
     * @param key  the key of the child data entry
     * @param type the data type of the child data entry
     * @return true if the object has an entry matching the key and the entry matches the data type
     */
    public boolean has(String key, DataType type)
    {
        DataEntry entry = this.children.get(key);
        return entry != null && entry.getType() == type;
    }

    /**
     * Gets a DataEntry for the given key. This is an abstract type and will need to be casted
     * to the correct type to access the data. See {@link DataEntry#getType()} to determine the type.
     *
     * @param key the key of the entry
     * @return A data entry instance or null if no entry with the name exists
     */
    @Nullable
    public DataEntry get(String key)
    {
        return this.children.get(key);
    }

    /**
     * Gets a DataObject for the given key. A cast exception will be thrown if the key returns a
     * data entry that is not the correct type, hence this should be checked using {@link #has(String, DataType)}
     * before calling this method. Alternatively, use {@link #get(String)} to return an abstract
     * type.
     *
     * @param key the key of the data object
     * @return a data object instance or null if doesn't exist
     */
    @Nullable
    public DataObject getDataObject(String key)
    {
        return (DataObject) this.children.get(key);
    }

    /**
     * Gets a DataArray for the given key. A cast exception will be thrown if the key returns a
     * data entry that is not the correct type, hence this should be checked using {@link #has(String, DataType)}
     * before calling this method. Alternatively, use {@link #get(String)} to return an abstract
     * type.
     *
     * @param key the key of the data array
     * @return a data array instance or null if doesn't exist
     */
    @Nullable
    public DataArray getDataArray(String key)
    {
        return (DataArray) this.children.get(key);
    }

    /**
     * Gets a DataString for the given key. A cast exception will be thrown if the key returns a
     * data entry that is not the correct type, hence this should be checked using {@link #has(String, DataType)}
     * before calling this method. Alternatively, use {@link #get(String)} to return an abstract
     * type.
     *
     * @param key the key of the data string
     * @return a data string instance or null if doesn't exist
     */
    @Nullable
    public DataString getDataString(String key)
    {
        return (DataString) this.children.get(key);
    }

    /**
     * Gets a DataNumber for the given key. A cast exception will be thrown if the key returns a
     * data entry that is not the correct type, hence this should be checked using {@link #has(String, DataType)}
     * before calling this method. Alternatively, use {@link #get(String)} to return an abstract
     * type.
     *
     * @param key the key of the data number
     * @return a data number instance or null if doesn't exist
     */
    @Nullable
    public DataNumber getDataNumber(String key)
    {
        return (DataNumber) this.children.get(key);
    }

    /**
     * Gets a DataBoolean for the given key. A cast exception will be thrown if the key returns a
     * data entry that is not the correct type, hence this should be checked using {@link #has(String, DataType)}
     * before calling this method. Alternatively, use {@link #get(String)} to return an abstract
     * type.
     *
     * @param key the key of the data boolean
     * @return a data boolean instance or null if doesn't exist
     */
    @Nullable
    public DataBoolean getDataBoolean(String key)
    {
        return (DataBoolean) this.children.get(key);
    }

    /**
     * Converts a Json Element into a Data Object. This method will return null if the Json Element
     * is not a Json Object.
     *
     * @param element the json element to convert, which must be a json object
     * @return a data object representation of the json element or null if unable to convert
     */
    @Nullable
    public static DataObject convert(@Nullable JsonElement element)
    {
        if(element != null && element.isJsonObject())
        {
            return new DataObject(element.getAsJsonObject());
        }
        return null;
    }

    private static ImmutableMap<String, DataEntry> construct(JsonObject object)
    {
        ImmutableMap.Builder<String, DataEntry> builder = new ImmutableMap.Builder<>();
        object.entrySet().forEach(mapEntry ->
        {
            DataEntry entry = convertElement(mapEntry.getValue());
            if(entry != null)
            {
                builder.put(mapEntry.getKey(), entry);
            }
        });
        return builder.build();
    }
}
