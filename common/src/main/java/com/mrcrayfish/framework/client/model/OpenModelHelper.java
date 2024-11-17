package com.mrcrayfish.framework.client.model;

import com.google.common.base.MoreObjects;
import com.mrcrayfish.framework.api.serialize.DataObject;
import com.mrcrayfish.framework.platform.ClientServices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
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

/**
 * Author: MrCrayfish
 */
public class OpenModelHelper
{
    public static DataObject getData(ModelResourceLocation location)
    {
        BakedModel model = ClientServices.CLIENT.getBakedModel(location);
        return model instanceof IOpenModel openModel ? openModel.getData() : DataObject.EMPTY;
    }

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
            BakedModel model = Minecraft.getInstance().getModelManager().getModel(ModelResourceLocation.inventory(location));
            if(model instanceof IOpenModel openModel)
            {
                return openModel.getData();
            }
        }
        return DataObject.EMPTY;
    }

    public static DataObject getData(ItemStack stack, @Nullable Level level, @Nullable LivingEntity entity, int seed)
    {
        BakedModel model = Minecraft.getInstance().getItemRenderer().getModel(stack, level, entity, seed);
        return model instanceof IOpenModel openModel ? openModel.getData() : DataObject.EMPTY;
    }
}
