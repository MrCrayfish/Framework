package com.mrcrayfish.framework.platform;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.mrcrayfish.framework.api.serialize.DataObject;
import com.mrcrayfish.framework.client.model.ForgeBakedOpenModel;
import com.mrcrayfish.framework.client.model.IOpenModel;
import com.mrcrayfish.framework.platform.services.IClientHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class ForgeClientHelper implements IClientHelper
{
    private static final BlockElement.Deserializer BLOCK_PART_DESERIALIZER = new BlockElement.Deserializer();

    @Override
    public BlockElement deserializeBlockElement(JsonElement element, JsonDeserializationContext context)
    {
        return BLOCK_PART_DESERIALIZER.deserialize(element, BlockElement.class, context);
    }

    @Override
    public BakedModel getBakedModel(ResourceLocation location)
    {
        return Minecraft.getInstance().getModelManager().getModel(location);
    }
}
