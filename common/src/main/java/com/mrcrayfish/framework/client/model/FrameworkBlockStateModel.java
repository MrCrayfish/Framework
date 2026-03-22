package com.mrcrayfish.framework.client.model;

import com.mrcrayfish.framework.api.client.model.FrameworkBakedModel;
import com.mrcrayfish.framework.api.serialize.DataObject;
import com.mrcrayfish.framework.util.Utils;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;

import java.util.List;

import static net.minecraft.client.resources.model.geometry.BakedQuad.MaterialFlags;

public record FrameworkBlockStateModel(FrameworkBakedModel model) implements BlockStateModel, IOpenModel
{
    public static final Identifier ID = Utils.rl("model");

    @Override
    public void collectParts(RandomSource source, List<BlockStateModelPart> list)
    {
        list.add(this.model);
    }

    @Override
    public Material.Baked particleMaterial()
    {
        return this.model.particleMaterial();
    }

    @Override
    @MaterialFlags
    public int materialFlags()
    {
        return this.model.materialFlags();
    }

    @Override
    public DataObject getData()
    {
        return this.model.getData();
    }
}
