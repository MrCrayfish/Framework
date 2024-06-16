package com.mrcrayfish.framework.platform.services;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;

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
     * Gets a BakedModel in the ModelManager using a ModelResourceLocation
     *
     * @param location the identifier of the baked model
     * @return the baked model or missing model if location doesn't exist
     */
    BakedModel getBakedModel(ModelResourceLocation location);

    /**
     * @return The platform specific variant for registering standalone models.
     */
    String getStandaloneModelVariant();
}
