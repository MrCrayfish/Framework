package com.mrcrayfish.framework.client.model;

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
public class ForgeBakedOpenModel extends BakedModelWrapper<BakedModel> implements IOpenModel
{
    private final DataObject data;

    public ForgeBakedOpenModel(BakedModel originalModel, @Nullable DataObject data)
    {
        super(originalModel);
        this.data = data;
    }

    @Override
    public DataObject getData()
    {
        return this.data;
    }
}
