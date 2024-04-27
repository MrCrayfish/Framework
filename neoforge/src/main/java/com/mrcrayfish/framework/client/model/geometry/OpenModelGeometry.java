package com.mrcrayfish.framework.client.model.geometry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mrcrayfish.framework.Constants;
import com.mrcrayfish.framework.api.serialize.DataObject;
import com.mrcrayfish.framework.client.model.NeoForgeBakedOpenModel;
import com.mrcrayfish.framework.client.model.OpenModelDeserializer;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.ElementsModel;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

import javax.annotation.Nullable;
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
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation)
    {
        return new NeoForgeBakedOpenModel(super.bake(context, baker, spriteGetter, modelState, overrides, modelLocation), this.data);
    }

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter, IGeometryBakingContext context)
    {
        this.model.resolveParents(modelGetter);
    }

    @EventBusSubscriber(modid = Constants.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
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
            event.register(new ResourceLocation(Constants.MOD_ID, "open_model"), new Loader());
        }
    }
}
