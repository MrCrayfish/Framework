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
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

/**
 * Author: MrCrayfish
 */
public class OpenModelGeometry extends ModelLoaderRegistry.VanillaProxy
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
    public BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation)
    {
        return new ForgeBakedOpenModel(super.bake(owner, bakery, spriteGetter, modelTransform, overrides, modelLocation), this.data);
    }

    @Override
    public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors)
    {
        // TODO do we need this?
        return this.model.getMaterials(modelGetter, missingTextureErrors);
    }

    @Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class Loader implements IModelLoader<ModelLoaderRegistry.VanillaProxy>
    {
        @Override
        public OpenModelGeometry read(JsonDeserializationContext context, JsonObject object)
        {
            return new OpenModelGeometry(OpenModelDeserializer.INSTANCE.deserialize(object, BlockModel.class, context), DataObject.convert(object.get("data")));
        }

        @SubscribeEvent
        public static void onModelRegister(ModelRegistryEvent event)
        {
            ModelLoaderRegistry.registerLoader(new ResourceLocation(Constants.MOD_ID, "open_model"), new Loader());
        }

        @Override
        public void onResourceManagerReload(ResourceManager manager) {}
    }
}
