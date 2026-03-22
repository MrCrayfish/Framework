package com.mrcrayfish.framework.platform;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.mrcrayfish.framework.api.client.model.FabricModelResource;
import com.mrcrayfish.framework.api.client.model.FrameworkModelBaker;
import com.mrcrayfish.framework.api.client.model.FrameworkModelResource;
import com.mrcrayfish.framework.platform.services.IClientHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.resources.model.cuboid.CuboidModelElement;
import net.minecraft.resources.Identifier;

import java.lang.reflect.Constructor;

/**
 * Author: MrCrayfish
 */
public class FabricClientHelper implements IClientHelper
{
    private static final JsonDeserializer<CuboidModelElement> BLOCK_PART_DESERIALIZER = createBlockElementDeserializerInstance();

    @Override
    public CuboidModelElement deserializeBlockElement(JsonElement element, JsonDeserializationContext context)
    {
        return BLOCK_PART_DESERIALIZER.deserialize(element, CuboidModelElement.class, context);
    }

    @Override
    public <T> T getStandaloneModel(FrameworkModelResource<T> resource)
    {
        throw new UnsupportedOperationException("Not supported yet.");
        //return Minecraft.getInstance().getModelManager().getModel(((FabricModelResource<T>) resource).extraModelKey());
    }

    @Override
    public <T> FrameworkModelResource<T> createModelResource(Identifier id, FrameworkModelBaker<T> baker)
    {
        return new FabricModelResource<>(id, baker);
    }

    @Override
    public void setScrollingState(AbstractSelectionList<?> list, boolean state)
    {
        list.scrolling = state;
    }

    @SuppressWarnings("unchecked")
    private static JsonDeserializer<CuboidModelElement> createBlockElementDeserializerInstance()
    {
        try
        {
            Class<?> innerClass = Class.forName(CuboidModelElement.class.getName() + "$Deserializer");
            Constructor<?> constructor = innerClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            return (JsonDeserializer<CuboidModelElement>) constructor.newInstance();
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
