package com.mrcrayfish.framework.platform.services;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.mrcrayfish.framework.api.client.model.FrameworkModelBaker;
import com.mrcrayfish.framework.api.client.model.FrameworkModelResource;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.resources.Identifier;

/**
 * Author: MrCrayfish
 */
public interface IClientHelper
{
    /**
     * Platform specific method to deserialize a block element instance
     *
     * @param element the json element of the block element
     * @param context the gson deserialization context
     * @return a block element instance
     */
    BlockElement deserializeBlockElement(JsonElement element, JsonDeserializationContext context);

    /**
     * Gets a BakedModel in the ModelManager using a ModelIdentifier
     *
     * @param key the identifier of the baked model
     * @return the baked model or missing model if location doesn't exist
     */
    <T> T getStandaloneModel(FrameworkModelResource<T> key);

    /**
     *
     * @param id
     * @param baker
     * @return
     * @param <T>
     */
    <T> FrameworkModelResource<T> createModelResource(Identifier id, FrameworkModelBaker<T> baker);

    ChunkSectionLayer getChunkSectionLayer(ResolvedModel model);

    ChunkSectionLayer getChunkSectionLayer(BlockModelPart part);
    /**
     * Sets the scrolling state of AbstractSelectionList. This field is private, so field is exposed
     * using AW/AT on subprojects.
     *
     * @param list  the AbstractSelectionList to update
     * @param state the new scrolling state
     */
    void setScrollingState(AbstractSelectionList<?> list, boolean state);

}
