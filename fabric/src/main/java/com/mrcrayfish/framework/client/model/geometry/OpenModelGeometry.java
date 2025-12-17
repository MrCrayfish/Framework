package com.mrcrayfish.framework.client.model.geometry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mrcrayfish.framework.api.serialize.DataObject;
import com.mrcrayfish.framework.client.model.IOpenModel;
import com.mrcrayfish.framework.client.model.OpenBlockModel;
import com.mrcrayfish.framework.util.Utils;
//import net.fabricmc.fabric.api.client.model.loading.v1.UnbakedModelDeserializer;
//import net.fabricmc.fabric.api.client.model.loading.v1.wrapper.WrapperUnbakedModel;
import net.fabricmc.fabric.api.client.model.loading.v1.UnbakedModelDeserializer;
import net.fabricmc.fabric.api.client.model.loading.v1.wrapper.WrapperUnbakedModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

public class OpenModelGeometry extends WrapperUnbakedModel implements IOpenModel
{
    private final DataObject data;

    public OpenModelGeometry(UnbakedModel wrapped, @Nullable DataObject data)
    {
        super(wrapped);
        this.data = data;
    }

    @Override
    public DataObject getData()
    {
        return this.data;
    }

    public static class Loader implements UnbakedModelDeserializer
    {
        public static final Identifier ID = Utils.rl("open_model");

        @Override
        public UnbakedModel deserialize(JsonObject object, JsonDeserializationContext context)
        {
           return new OpenModelGeometry(OpenBlockModel.Deserializer.INSTANCE.deserialize(object, OpenBlockModel.class, context), DataObject.convertNonNull(object.get("data")));
        }
    }
}
