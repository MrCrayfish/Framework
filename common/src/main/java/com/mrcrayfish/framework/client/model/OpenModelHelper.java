package com.mrcrayfish.framework.client.model;

import com.mrcrayfish.framework.api.serialize.DataObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Author: MrCrayfish
 */
public class OpenModelHelper
{
    public static DataObject getData(BlockState state)
    {
        BlockStateModel model = Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(state);
        return model instanceof IOpenModel openModel ? openModel.getData() : DataObject.EMPTY;
    }

    public static DataObject getData(Item item)
    {
        Identifier location = item.components().get(DataComponents.ITEM_MODEL);
        if(location != null)
        {
            return readDataFromTopLevelItemModel(location);
        }
        return DataObject.EMPTY;
    }

    public static DataObject getData(ItemStack stack)
    {
        Identifier location = stack.get(DataComponents.ITEM_MODEL);
        if(location != null)
        {
            return readDataFromTopLevelItemModel(location);
        }
        return DataObject.EMPTY;
    }

    private static DataObject readDataFromTopLevelItemModel(Identifier location)
    {
        ItemModel model = Minecraft.getInstance().getModelManager().getItemModel(location);
        if(model instanceof IOpenModel openModel)
        {
            return openModel.getData();
        }
        return DataObject.EMPTY;
    }
}
