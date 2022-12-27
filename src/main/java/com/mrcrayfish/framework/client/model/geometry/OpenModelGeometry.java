package com.mrcrayfish.framework.client.model.geometry;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Vector3f;
import com.mrcrayfish.framework.Reference;
import com.mrcrayfish.framework.api.serialize.DataObject;
import com.mrcrayfish.framework.client.model.BakedOpenModel;
import com.mrcrayfish.framework.util.GsonUtils;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementRotation;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * Author: MrCrayfish
 */
public class OpenModelGeometry implements IUnbakedGeometry<OpenModelGeometry>
{
    private final BlockModel model;
    private final DataObject data;

    public OpenModelGeometry(BlockModel model, @Nullable DataObject data)
    {
        this.model = model;
        this.data = data;
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation)
    {
        return new BakedOpenModel(this.model.bake(bakery, this.model, spriteGetter, modelTransform, modelLocation, true), this.data);
    }

    @Override
    public Collection<Material> getMaterials(IGeometryBakingContext context, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors)
    {
        return this.model.getMaterials(modelGetter, missingTextureErrors);
    }

    @Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class Loader implements IGeometryLoader<OpenModelGeometry>
    {
        @Override
        public OpenModelGeometry read(JsonObject object, JsonDeserializationContext context) throws JsonParseException
        {
            return new OpenModelGeometry(Deserializer.INSTANCE.deserialize(object, BlockModel.class, context), DataObject.convert(object.get("data")));
        }

        @SubscribeEvent
        public static void onModelRegister(ModelEvent.RegisterGeometryLoaders event)
        {
            event.register("open_model", new Loader());
        }
    }

    public static class Deserializer extends BlockModel.Deserializer
    {
        private static final BlockElement.Deserializer BLOCK_PART_DESERIALIZER = new BlockElement.Deserializer();
        private static final Deserializer INSTANCE = new Deserializer();

        /**
         * Reads the bl
         */
        @Override
        protected List<BlockElement> getElements(JsonDeserializationContext context, JsonObject object)
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
            BlockElement e = BLOCK_PART_DESERIALIZER.deserialize(element, BlockElement.class, context);
            BlockElementRotation r = e.rotation != null ? new BlockElementRotation(e.rotation.origin, e.rotation.axis, angle, e.rotation.rescale) : null;
            return new BlockElement(from, to, e.faces, r, e.shade);
        }
    }
}
