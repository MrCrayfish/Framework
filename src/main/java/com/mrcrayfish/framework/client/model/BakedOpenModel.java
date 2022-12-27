package com.mrcrayfish.framework.client.model;

import com.google.gson.JsonObject;
import com.mrcrayfish.framework.api.serialize.DataObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.BakedModelWrapper;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class BakedOpenModel extends BakedModelWrapper<BakedModel>
{
    private final DataObject data;

    public BakedOpenModel(BakedModel originalModel, @Nullable DataObject data)
    {
        super(originalModel);
        this.data = data;
    }

    public static DataObject getData(ResourceLocation modelLocation)
    {
        BakedModel model = Minecraft.getInstance().getModelManager().getModel(modelLocation);
        return model instanceof BakedOpenModel openModel ? openModel.data : DataObject.EMPTY;
    }

    public static DataObject getData(BlockState state)
    {
        BakedModel model = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(state);
        return model instanceof BakedOpenModel openModel ? openModel.data : DataObject.EMPTY;
    }

    public static DataObject getData(Item item)
    {
        BakedModel model = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getItemModel(item);
        return model instanceof BakedOpenModel openModel ? openModel.data : DataObject.EMPTY;
    }

    public static DataObject getData(ItemStack stack, @Nullable Level level, @Nullable LivingEntity entity, int seed)
    {
        BakedModel model = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getItemModel(stack.getItem());
        if(model != null)
        {
            ClientLevel clientLevel = level instanceof ClientLevel ? (ClientLevel) level : null;
            BakedModel overrideModel = model.getOverrides().resolve(model, stack, clientLevel, entity, seed);
            return overrideModel instanceof BakedOpenModel openModel ? openModel.data : DataObject.EMPTY;
        }
        return DataObject.EMPTY;
    }
}
