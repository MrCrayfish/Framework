package com.mrcrayfish.framework.platform;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.mrcrayfish.framework.api.client.model.FrameworkModelKey;
import com.mrcrayfish.framework.platform.services.IClientHelper;
import net.minecraft.client.renderer.block.model.BlockElement;

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
    public <T> T getStandaloneModel(FrameworkModelKey<T> key)
    {
        // TODO update with fabric api is reimplements.
        /*ModelManager manager = Minecraft.getInstance().getModelManager();
        return manager.getModel(location);*/
        return null;
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
