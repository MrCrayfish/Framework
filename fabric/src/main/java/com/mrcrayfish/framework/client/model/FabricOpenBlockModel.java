package com.mrcrayfish.framework.client.model;

import com.mrcrayfish.framework.api.serialize.DataObject;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public class FabricOpenBlockModel extends BlockModel
{
    private final DataObject data;

    public FabricOpenBlockModel(@Nullable ResourceLocation id, List<BlockElement> elements, TextureSlots.Data materials, @Nullable Boolean ambientOcc, @Nullable UnbakedModel.GuiLight light, ItemTransforms transforms, @Nullable DataObject data)
    {
        super(id, elements, materials, ambientOcc, light, transforms);
        this.data = data;
    }

    @Override
    public BakedModel bake(TextureSlots slots, ModelBaker baker, ModelState state, boolean bl, boolean bl2, ItemTransforms transforms)
    {
        return new FabricBakedOpenModel(super.bake(slots, baker, state, bl, bl2, transforms), this.data);
    }
}
