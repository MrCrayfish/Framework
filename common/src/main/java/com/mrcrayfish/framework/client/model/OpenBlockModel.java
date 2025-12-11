package com.mrcrayfish.framework.client.model;

import com.google.gson.*;
import com.mrcrayfish.framework.api.serialize.DataObject;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.resources.model.UnbakedGeometry;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.function.Function;

public record OpenBlockModel(@Nullable UnbakedGeometry geometry, @Nullable UnbakedModel.GuiLight guiLight, @Nullable Boolean ambientOcclusion, @Nullable ItemTransforms transforms, TextureSlots.Data textureSlots, @Nullable Identifier parent, DataObject data) implements UnbakedModel
{
    public static class Deserializer implements JsonDeserializer<OpenBlockModel>
    {
        public static final Deserializer INSTANCE = new Deserializer();

        @Override
        public OpenBlockModel deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException
        {
            JsonObject object = element.getAsJsonObject();
            UnbakedGeometry geometry = this.createGeometry(context, object);
            Identifier parent = this.parseString(object, "parent", Identifier::parse, null);
            TextureSlots.Data textures = this.parseObject(object, "textures", o -> TextureSlots.parseTextureMap(o), TextureSlots.Data.EMPTY);
            Boolean ambientOcclusion = this.parseString(object, "ambientocclusion", Boolean::parseBoolean, null);
            ItemTransforms transforms = this.parseObject(object, "display", o -> context.deserialize(o, ItemTransforms.class), null);
            UnbakedModel.GuiLight guiLight = this.parseString(object, "gui_light", UnbakedModel.GuiLight::getByName, null);
            DataObject data = DataObject.convertNonNull(object.get("data"));
            return new OpenBlockModel(geometry, guiLight, ambientOcclusion, transforms, textures, parent, data);
        }

        @Nullable
        private UnbakedGeometry createGeometry(JsonDeserializationContext context, JsonObject object)
        {
            return OpenModelDeserializer.INSTANCE.getElements(context, object);
        }

        @Nullable
        private <T> T parseString(JsonObject object, String key, Function<String, T> parser, @Nullable T defaultValue)
        {
            return object.has(key) ? parser.apply(GsonHelper.getAsString(object, key)) : defaultValue;
        }

        @Nullable
        private <T> T parseObject(JsonObject object, String key, Function<JsonObject, T> parser, @Nullable T defaultValue)
        {
            return object.has(key) ? parser.apply(GsonHelper.getAsJsonObject(object, key)) : defaultValue;
        }
    }
}
