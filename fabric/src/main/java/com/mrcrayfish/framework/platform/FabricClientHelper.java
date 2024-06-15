package com.mrcrayfish.framework.platform;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.mrcrayfish.framework.platform.services.IClientHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;

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
    public BakedModel getBakedModel(ModelResourceLocation location)
    {
        ModelManager manager = Minecraft.getInstance().getModelManager();
        return manager.bakedRegistry.getOrDefault(location, manager.getMissingModel());
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
