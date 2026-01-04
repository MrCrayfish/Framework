package com.mrcrayfish.framework.platform;

import com.google.common.base.MoreObjects;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.mrcrayfish.framework.api.client.model.FrameworkBakedModel;
import com.mrcrayfish.framework.api.client.model.FrameworkModelBaker;
import com.mrcrayfish.framework.api.client.model.FrameworkModelResource;
import com.mrcrayfish.framework.api.client.model.NeoForgeModelResource;
import com.mrcrayfish.framework.platform.services.IClientHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.SimpleModelWrapper;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.RenderTypeGroup;
import net.neoforged.neoforge.client.model.NeoForgeModelProperties;

/**
 * Author: MrCrayfish
 */
public class NeoForgeClientHelper implements IClientHelper
{
    private static final BlockElement.Deserializer BLOCK_PART_DESERIALIZER = new BlockElement.Deserializer();

    @Override
    public BlockElement deserializeBlockElement(JsonElement element, JsonDeserializationContext context)
    {
        return BLOCK_PART_DESERIALIZER.deserialize(element, BlockElement.class, context);
    }

    @Override
    public <T> T getStandaloneModel(FrameworkModelResource<T> resource)
    {
        return Minecraft.getInstance().getModelManager().getStandaloneModel(((NeoForgeModelResource<T>) resource).standaloneKey());
    }

    @Override
    public <T> FrameworkModelResource<T> createModelResource(Identifier id, FrameworkModelBaker<T> baker)
    {
        return new NeoForgeModelResource<>(id, baker);
    }

    @Override
    public ChunkSectionLayer getChunkSectionLayer(ResolvedModel model)
    {
        RenderTypeGroup group = model.getTopAdditionalProperties().getOptional(NeoForgeModelProperties.RENDER_TYPE);
        return group != null && !group.isEmpty() ? group.block() : ChunkSectionLayer.SOLID;
    }

    @Override
    public ChunkSectionLayer getChunkSectionLayer(BlockModelPart part)
    {
        return MoreObjects.firstNonNull(switch(part) {
            case SimpleModelWrapper wrapper -> wrapper.renderType();
            case FrameworkBakedModel model -> model.layer();
            default -> null;
        }, ChunkSectionLayer.SOLID);
    }

    @Override
    public void setScrollingState(AbstractSelectionList<?> list, boolean state)
    {
        list.scrolling = state;
    }
}
