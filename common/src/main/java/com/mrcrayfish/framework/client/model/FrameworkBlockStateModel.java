package com.mrcrayfish.framework.client.model;

import com.mrcrayfish.framework.api.serialize.DataObject;
import com.mrcrayfish.framework.util.Utils;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

import java.util.List;

public record FrameworkBlockStateModel(BlockStateModel model, DataObject data) implements BlockStateModel, IOpenModel
{
    public static final ResourceLocation ID = Utils.rl("model");

    @Override
    public void collectParts(RandomSource source, List<BlockModelPart> list)
    {
        this.model.collectParts(source, list);
    }

    @Override
    public TextureAtlasSprite particleIcon()
    {
        return this.model.particleIcon();
    }

    @Override
    public DataObject getData()
    {
        return this.data;
    }
}
