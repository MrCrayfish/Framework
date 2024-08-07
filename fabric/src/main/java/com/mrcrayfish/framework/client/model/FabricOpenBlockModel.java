package com.mrcrayfish.framework.client.model;

import com.mojang.datafixers.util.Either;
import com.mrcrayfish.framework.api.serialize.DataObject;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Author: MrCrayfish
 */
public class FabricOpenBlockModel extends BlockModel
{
    private final DataObject data;

    public FabricOpenBlockModel(@Nullable ResourceLocation resourceLocation, List<BlockElement> elements, Map<String, Either<Material, String>> materials, boolean ambientOcc, @Nullable BlockModel.GuiLight light, ItemTransforms transforms, List<ItemOverride> overrides, @Nullable DataObject data)
    {
        super(resourceLocation, elements, materials, ambientOcc, light, transforms, overrides);
        this.data = data;
    }

    @Override
    public BakedModel bake(ModelBakery bakery, BlockModel model, Function<Material, TextureAtlasSprite> function, ModelState state, ResourceLocation location, boolean bl)
    {
        return new FabricBakedOpenModel(super.bake(bakery, model, function, state, location, bl), this.data);
    }
}
