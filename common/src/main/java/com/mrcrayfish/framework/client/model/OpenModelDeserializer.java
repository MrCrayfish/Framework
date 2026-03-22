package com.mrcrayfish.framework.client.model;

import com.google.gson.*;
import com.mrcrayfish.framework.platform.ClientServices;
import com.mrcrayfish.framework.util.GsonUtils;
import net.minecraft.client.resources.model.cuboid.CuboidModel;
import net.minecraft.client.resources.model.cuboid.CuboidModelElement;
import net.minecraft.client.resources.model.cuboid.UnbakedCuboidGeometry;
import net.minecraft.client.resources.model.geometry.UnbakedGeometry;
import net.minecraft.util.GsonHelper;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class OpenModelDeserializer extends CuboidModel.Deserializer
{
    // TODO investigate if not needed and can merge into OpenBlockModel
    public static final OpenModelDeserializer INSTANCE = new OpenModelDeserializer();

    /**
     * Reads the bl
     */
    @Override
    public UnbakedGeometry getElements(JsonDeserializationContext context, JsonObject object) throws JsonParseException
    {
        List<CuboidModelElement> list = new ArrayList<>();
        for(JsonElement element : GsonHelper.getAsJsonArray(object, "components", new JsonArray()))
        {
            list.add(this.readCuboidElement(element, context));
        }
        return new UnbakedCuboidGeometry(list);
    }

    /**
     * Reads a block element without restrictions on the size and rotation angle.
     */
    @SuppressWarnings("ConstantConditions")
    private CuboidModelElement readCuboidElement(JsonElement element, JsonDeserializationContext context)
    {
        JsonObject object = element.getAsJsonObject();

        // Get copy of custom size and angle properties
        Vector3f from = GsonUtils.getVector3f(object, "from");
        Vector3f to = GsonUtils.getVector3f(object, "to");

        // Make valid for vanilla block element deserializer
        JsonArray zero = new JsonArray();
        zero.add(0F);
        zero.add(0F);
        zero.add(0F);
        object.add("from", zero);
        object.add("to", zero);

        // Read vanilla element and construct new element with custom properties
        CuboidModelElement e = ClientServices.CLIENT.deserializeBlockElement(element, context);
        return new CuboidModelElement(from, to, e.faces(), e.rotation(), e.shade(), e.lightEmission());
    }
}
