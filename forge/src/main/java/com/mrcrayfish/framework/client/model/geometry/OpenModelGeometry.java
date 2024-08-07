package com.mrcrayfish.framework.client.model.geometry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import com.mrcrayfish.framework.Constants;
import com.mrcrayfish.framework.api.serialize.DataObject;
import com.mrcrayfish.framework.client.model.ForgeBakedOpenModel;
import com.mrcrayfish.framework.client.model.OpenModelDeserializer;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.model.ElementsModel;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

/**
 * Author: MrCrayfish
 */
public class OpenModelGeometry extends ElementsModel
{
    private final BlockModel model;
    private final DataObject data;

    public OpenModelGeometry(BlockModel model, @Nullable DataObject data)
    {
        super(model.getElements());
        this.model = model;
        this.data = data;
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation)
    {
        return new ForgeBakedOpenModel(super.bake(context, bakery, spriteGetter, modelState, overrides, modelLocation), this.data);
    }

    @Override
    public Collection<Material> getMaterials(IGeometryBakingContext context, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors)
    {
        return this.model.getMaterials(modelGetter, missingTextureErrors);
    }

    @Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class Loader implements IGeometryLoader<ElementsModel>
    {
        @Override
        public OpenModelGeometry read(JsonObject object, JsonDeserializationContext context) throws JsonParseException
        {
            return new OpenModelGeometry(OpenModelDeserializer.INSTANCE.deserialize(object, BlockModel.class, context), DataObject.convert(object.get("data")));
        }

        @SubscribeEvent
        public static void onModelRegister(ModelEvent.RegisterGeometryLoaders event)
        {
            event.register("open_model", new Loader());
        }
    }
}
