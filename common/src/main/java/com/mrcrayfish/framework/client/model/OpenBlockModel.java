package com.mrcrayfish.framework.client.model;

import com.google.gson.*;
import com.mrcrayfish.framework.api.serialize.DataObject;
import com.mrcrayfish.framework.util.reflection.ReflectedMethod;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.resources.model.UnbakedGeometry;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

public record OpenBlockModel(@Nullable UnbakedGeometry geometry, @Nullable UnbakedModel.GuiLight guiLight, @Nullable Boolean ambientOcclusion, @Nullable ItemTransforms transforms, TextureSlots.Data textureSlots, @Nullable ResourceLocation parent, DataObject data) implements UnbakedModel
{
    public static class Deserializer implements JsonDeserializer<OpenBlockModel>
    {
        public static final Deserializer INSTANCE = new Deserializer();
        // Use as much as we can from the original block model deserializer
        private static final BlockModel.Deserializer BLOCK_MODEL_DESERIALIZER = new BlockModel.Deserializer();
        private static final ReflectedMethod<BlockModel.Deserializer, String> GET_PARENT = new ReflectedMethod<>(BlockModel.Deserializer.class, "getParentName", JsonObject.class);
        private static final ReflectedMethod<BlockModel.Deserializer, TextureSlots.Data> GET_TEXTURES = new ReflectedMethod<>(BlockModel.Deserializer.class, "getTextureMap", JsonObject.class);
        private static final ReflectedMethod<BlockModel.Deserializer, Boolean> GET_AMBIENT_OCCLUSION = new ReflectedMethod<>(BlockModel.Deserializer.class, "getAmbientOcclusion", JsonObject.class);

        @Override
        public OpenBlockModel deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException
        {
            JsonObject object = element.getAsJsonObject();
            UnbakedGeometry geometry = this.createGeometry(context, object);
            ResourceLocation parent = this.parseParent(GET_PARENT.invoke(BLOCK_MODEL_DESERIALIZER, object));
            TextureSlots.Data textures = GET_TEXTURES.invoke(BLOCK_MODEL_DESERIALIZER, object);
            Boolean ambientOcclusion = GET_AMBIENT_OCCLUSION.invoke(BLOCK_MODEL_DESERIALIZER, object);
            ItemTransforms transforms = this.createTransforms(context, object);
            UnbakedModel.GuiLight guiLight = this.createGuiLight(object);
            DataObject data = DataObject.convertNonNull(object.get("data"));
            return new OpenBlockModel(geometry, guiLight, ambientOcclusion, transforms, textures, parent, data);
        }

        @Nullable
        private UnbakedGeometry createGeometry(JsonDeserializationContext context, JsonObject object)
        {
            return OpenModelDeserializer.INSTANCE.getElements(context, object);
        }

        @Nullable
        private ItemTransforms createTransforms(JsonDeserializationContext context, JsonObject object)
        {
            if(object.has("display"))
            {
                JsonObject display = GsonHelper.getAsJsonObject(object, "display");
                return context.deserialize(display, ItemTransforms.class);
            }
            return null;
        }

        @Nullable
        private UnbakedModel.GuiLight createGuiLight(JsonObject object)
        {
            return object.has("gui_light") ? UnbakedModel.GuiLight.getByName(GsonHelper.getAsString(object, "gui_light")) : null;
        }

        @Nullable
        private ResourceLocation parseParent(String parent)
        {
            return !parent.isBlank() ? ResourceLocation.tryParse(parent) : null;
        }
    }
}
