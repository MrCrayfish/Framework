package com.mrcrayfish.framework.client.model;

import com.mrcrayfish.framework.api.serialize.DataObject;
import com.mrcrayfish.framework.platform.ClientServices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Author: MrCrayfish
 */
public class OpenModelHelper
{
    public static DataObject getData(BlockState state)
    {
        BakedModel model = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(state);
        return model instanceof IOpenModel openModel ? openModel.getData() : DataObject.EMPTY;
    }

    public static DataObject getData(Item item)
    {
        ResourceLocation location = item.components().get(DataComponents.ITEM_MODEL);
        if(location != null)
        {
            return readDataFromTopLevelItemModel(location);
        }
        return DataObject.EMPTY;
    }

    public static DataObject getData(ItemStack stack)
    {
        ResourceLocation location = stack.get(DataComponents.ITEM_MODEL);
        if(location != null)
        {
            return readDataFromTopLevelItemModel(location);
        }
        return DataObject.EMPTY;
    }

    private static DataObject readDataFromTopLevelItemModel(ResourceLocation location)
    {
        ItemModel model = Minecraft.getInstance().getModelManager().getItemModel(location);
        if(model instanceof IOpenModel openModel)
        {
            return openModel.getData();
        }
        return DataObject.EMPTY;
    }
}
