package com.mrcrayfish.framework.api.client.model;

import com.mrcrayfish.framework.api.serialize.DataObject;
import com.mrcrayfish.framework.client.model.IOpenModel;
import com.mrcrayfish.framework.platform.ClientServices;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.QuadCollection;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record FrameworkBakedModel(QuadCollection quads, boolean useAmbientOcclusion, TextureAtlasSprite particleIcon, ChunkSectionLayer layer, DataObject data) implements BlockModelPart, IOpenModel
{
    public static final FrameworkModelBaker<FrameworkBakedModel> BAKER = (model, baker) -> {
        boolean ambientOcclusion = model.getTopAmbientOcclusion();
        TextureSlots textureSlots = model.getTopTextureSlots();
        QuadCollection quads = model.bakeTopGeometry(textureSlots, baker, BlockModelRotation.IDENTITY); // TODO check
        TextureAtlasSprite particle = model.resolveParticleSprite(textureSlots, baker);
        ChunkSectionLayer layer = ClientServices.CLIENT.getChunkSectionLayer(model);
        DataObject data = model.wrapped() instanceof IOpenModel openModel ? openModel.getData() : DataObject.EMPTY;
        return new FrameworkBakedModel(quads, ambientOcclusion, particle, layer, data);
    };

    @Override
    public List<BakedQuad> getQuads(@Nullable Direction direction)
    {
        return this.quads.getQuads(direction);
    }

    @Override
    public DataObject getData()
    {
        return this.data;
    }
}
