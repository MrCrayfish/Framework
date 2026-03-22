package com.mrcrayfish.framework.platform;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.mrcrayfish.framework.api.client.model.FrameworkModelBaker;
import com.mrcrayfish.framework.api.client.model.FrameworkModelResource;
import com.mrcrayfish.framework.api.client.model.NeoForgeModelResource;
import com.mrcrayfish.framework.platform.services.IClientHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.resources.model.cuboid.CuboidModelElement;
import net.minecraft.resources.Identifier;

/**
 * Author: MrCrayfish
 */
public class NeoForgeClientHelper implements IClientHelper
{
    private static final CuboidModelElement.Deserializer BLOCK_PART_DESERIALIZER = new CuboidModelElement.Deserializer();

    @Override
    public CuboidModelElement deserializeBlockElement(JsonElement element, JsonDeserializationContext context)
    {
        return BLOCK_PART_DESERIALIZER.deserialize(element, CuboidModelElement.class, context);
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
    public void setScrollingState(AbstractSelectionList<?> list, boolean state)
    {
        list.scrolling = state;
    }
}
