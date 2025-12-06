package com.mrcrayfish.framework.platform;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.mrcrayfish.framework.api.client.model.FabricModelResource;
import com.mrcrayfish.framework.api.client.model.FrameworkModelBaker;
import com.mrcrayfish.framework.api.client.model.FrameworkModelResource;
import com.mrcrayfish.framework.platform.services.IClientHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.resources.Identifier;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Author: MrCrayfish
 */
public class FabricClientHelper implements IClientHelper
{
    private static final BlockElement.Deserializer BLOCK_PART_DESERIALIZER = createBlockElementDeserializerInstance();

    @Override
    public BlockElement deserializeBlockElement(JsonElement element, JsonDeserializationContext context)
    {
        return BLOCK_PART_DESERIALIZER.deserialize(element, BlockElement.class, context);
    }

    @Override
    public <T> T getStandaloneModel(FrameworkModelResource<T> resource)
    {
        return Minecraft.getInstance().getModelManager().getModel(((FabricModelResource<T>) resource).extraModelKey());
    }

    @Override
    public <T> FrameworkModelResource<T> createModelResource(Identifier id, FrameworkModelBaker<T> baker)
    {
        return new FabricModelResource<>(id, baker);
    }

    @Override
    public ChunkSectionLayer getChunkSectionLayer(ResolvedModel model)
    {
        return ChunkSectionLayer.SOLID;
    }

    @Override
    public ChunkSectionLayer getChunkSectionLayer(BlockModelPart part)
    {
        return ChunkSectionLayer.SOLID;
    }

    @Override
    public void setScrollingState(AbstractSelectionList<?> list, boolean state)
    {
        list.scrolling = state;
    }

    private static BlockElement.Deserializer createBlockElementDeserializerInstance()
    {
        try
        {
            Constructor<BlockElement.Deserializer> constructor = BlockElement.Deserializer.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        }
        catch(NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }
}
