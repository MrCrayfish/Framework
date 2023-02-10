package com.mrcrayfish.framework.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.GsonHelper;
import org.joml.Vector3f;

/**
 * Author: MrCrayfish
 */
public class GsonUtils
{
    public static Vector3f getVector3f(JsonObject object, String name) throws JsonParseException
    {
        if(!object.has(name)) throw new JsonParseException("Missing member " + name);
        JsonArray array = GsonHelper.getAsJsonArray(object, name);
        if(array.size() != 3) throw new JsonParseException("Expected 3 " + name + " values, found: " + array.size());
        float x = GsonHelper.convertToFloat(array.get(0), name + "[0]");
        float y = GsonHelper.convertToFloat(array.get(1), name + "[1]");
        float z = GsonHelper.convertToFloat(array.get(2), name + "[2]");
        return new Vector3f(x, y, z);
    }
}
