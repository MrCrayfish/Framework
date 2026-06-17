package com.mrcrayfish.framework.api.client.model;

import com.mrcrayfish.framework.api.serialize.DataObject;
import com.mrcrayfish.framework.client.model.IOpenModel;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.BakedQuad.MaterialFlags;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

import java.util.List;

public record FrameworkBakedModel(QuadCollection quads, boolean useAmbientOcclusion, Material.Baked particleMaterial, ChunkSectionLayer layer, DataObject data) implements BlockStateModelPart, IOpenModel
{
    public static final FrameworkModelBaker<FrameworkBakedModel> BAKER = (model, baker) -> {
        boolean ambientOcclusion = model.getTopAmbientOcclusion();
        TextureSlots textureSlots = model.getTopTextureSlots();
        QuadCollection quads = model.bakeTopGeometry(textureSlots, baker, BlockModelRotation.IDENTITY);
        Material.Baked particle = model.resolveParticleMaterial(textureSlots, baker);
        ChunkSectionLayer layer = ChunkSectionLayer.SOLID;
        DataObject data = model.wrapped() instanceof IOpenModel openModel ? openModel.getData() : DataObject.EMPTY;
        return new FrameworkBakedModel(quads, ambientOcclusion, particle, layer, data);
    };

    @Override
    public List<BakedQuad> getQuads(@Nullable Direction direction)
    {
        return this.quads.getQuads(direction);
    }

    @Override
    @MaterialFlags
    public int materialFlags()
    {
        return this.quads.materialFlags();
    }

    @Override
    public DataObject getData()
    {
        return this.data;
    }
}
