package com.mrcrayfish.framework.api.serialize;

/**
 * Author: MrCrayfish
 */
public final class DataHelper
{
    /**
     * Retrieves a boolean from a DataObject for the given key. If the entry in the object does not
     * exist or the entry is not a DataBoolean, the default value will be returned instead.
     *
     * @param object       the DataObject to search for the entry
     * @param key          the key of the entry
     * @param defaultValue the default value as a fallback
     * @return an int or the default value if not found or wrong data type
     */
    public static boolean getBooleanOrDefault(DataObject object, String key, boolean defaultValue)
    {
        if(object.get(key) instanceof DataBoolean entry)
        {
            return entry.asBoolean();
        }
        return defaultValue;
    }

    /**
     * Retrieves an int from a DataObject for the given key. If the entry in the object does not
     * exist or the entry is not a DataNumber, the default value will be returned instead.
     *
     * @param object       the DataObject to search for the entry
     * @param key          the key of the entry
     * @param defaultValue the default value as a fallback
     * @return an int or the default value if not found or wrong data type
     */
    public static int getIntOrDefault(DataObject object, String key, int defaultValue)
    {
        if(object.get(key) instanceof DataNumber entry)
        {
            return entry.asInt();
        }
        return defaultValue;
    }

    /**
     * Retrieves a long from a DataObject for the given key. If the entry in the object does not
     * exist or the entry is not a DataNumber, the default value will be returned instead.
     *
     * @param object       the DataObject to search for the entry
     * @param key          the key of the entry
     * @param defaultValue the default value as a fallback
     * @return a long or the default value if not found or wrong data type
     */
    public static long getLongOrDefault(DataObject object, String key, long defaultValue)
    {
        if(object.get(key) instanceof DataNumber entry)
        {
            return entry.asLong();
        }
        return defaultValue;
    }

    /**
     * Retrieves a float from a DataObject for the given key. If the entry in the object does not
     * exist or the entry is not a DataNumber, the default value will be returned instead.
     *
     * @param object       the DataObject to search for the entry
     * @param key          the key of the entry
     * @param defaultValue the default value as a fallback
     * @return a float or the default value if not found or wrong data type
     */
    public static float getFloatOrDefault(DataObject object, String key, float defaultValue)
    {
        if(object.get(key) instanceof DataNumber entry)
        {
            return entry.asFloat();
        }
        return defaultValue;
    }

    /**
     * Retrieves a double from a DataObject for the given key. If the entry in the object does not
     * exist or the entry is not a DataNumber, the default value will be returned instead.
     *
     * @param object       the DataObject to search for the entry
     * @param key          the key of the entry
     * @param defaultValue the default value as a fallback
     * @return a double or the default value if not found or wrong data type
     */
    public static double getDoubleOrDefault(DataObject object, String key, double defaultValue)
    {
        if(object.get(key) instanceof DataNumber entry)
        {
            return entry.asDouble();
        }
        return defaultValue;
    }

    /**
     * Retrieves a String from a DataObject for the given key. If the entry in the object does not
     * exist or the entry is not a DataString, the default value will be returned instead.
     *
     * @param object       the DataObject to search for the entry
     * @param key          the key of the entry
     * @param defaultValue the default value as a fallback
     * @return a String or the default value if not found or wrong data type
     */
    public static String getStringOrDefault(DataObject object, String key, String defaultValue)
    {
        if(object.get(key) instanceof DataString entry)
        {
            return entry.asString();
        }
        return defaultValue;
    }

    /**
     * Retrieves a DataObject from a path. The path array should contain the keys to traverse in
     * order to reach the DataObject. The path must be in the correct order, starting from the root.
     *
     * @param root the DataObject to search for the entry
     * @param path   the path to the object
     * @return a DataObject or an empty DataObject if not found
     */
    @SuppressWarnings("DataFlowIssue")
    public static DataObject getDataObjectFromPath(DataObject root, String... path)
    {
        DataObject result = root;
        for(String s : path)
        {
            if(!result.has(s, DataType.OBJECT))
                return DataObject.EMPTY;
            result = result.getDataObject(s);
        }
        return result != root || path.length == 0 ? result : DataObject.EMPTY;
    }
}
