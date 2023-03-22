package com.mrcrayfish.framework.client.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mrcrayfish.framework.platform.ClientServices;
import com.mrcrayfish.framework.platform.Services;
import com.mrcrayfish.framework.util.GsonUtils;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementRotation;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.util.GsonHelper;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class OpenModelDeserializer extends BlockModel.Deserializer
{
    public static final OpenModelDeserializer INSTANCE = new OpenModelDeserializer();

    /**
     * Reads the bl
     */
    @Override
    public List<BlockElement> getElements(JsonDeserializationContext context, JsonObject object)
    {
        try
        {
            List<BlockElement> list = new ArrayList<>();
            for(JsonElement element : GsonHelper.getAsJsonArray(object, "components", new JsonArray()))
            {
                list.add(this.readBlockElement(element, context));
            }
            return list;
        }
        catch(Exception e)
        {
            throw new JsonParseException(e);
        }
    }

    /**
     * Reads a block element without restrictions on the size and rotation angle.
     */
    @SuppressWarnings("ConstantConditions")
    private BlockElement readBlockElement(JsonElement element, JsonDeserializationContext context) throws Exception
    {
        JsonObject object = element.getAsJsonObject();

        // Get copy of custom size and angle properties
        Vector3f from = GsonUtils.getVector3f(object, "from");
        Vector3f to = GsonUtils.getVector3f(object, "to");
        JsonObject rotation = GsonHelper.getAsJsonObject(object, "rotation", new JsonObject());
        float angle = GsonHelper.getAsFloat(rotation, "angle", 0F);

        // Make valid for vanilla block element deserializer
        JsonArray zero = new JsonArray();
        zero.add(0F);
        zero.add(0F);
        zero.add(0F);
        object.add("from", zero);
        object.add("to", zero);
        rotation.addProperty("angle", 0F);

        // Read vanilla element and construct new element with custom properties
        BlockElement e = ClientServices.CLIENT.deserializeBlockElement(element, context);
        BlockElementRotation r = e.rotation != null ? new BlockElementRotation(e.rotation.origin(), e.rotation.axis(), angle, e.rotation.rescale()) : null;
        return new BlockElement(from, to, e.faces, r, e.shade);
    }
}
