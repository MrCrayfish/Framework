package com.mrcrayfish.framework.client.model;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.framework.api.serialize.DataObject;
import com.mrcrayfish.framework.util.Utils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemModels;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Author: MrCrayfish
 */
public record FrameworkItemModel(ItemModel model, DataObject data) implements ItemModel, IOpenModel
{
    public static final ResourceLocation ID = Utils.rl("model");

    @Override
    public DataObject getData()
    {
        return this.data;
    }

    @Override
    public void update(ItemStackRenderState state, ItemStack stack, ItemModelResolver resolver, ItemDisplayContext context, @Nullable ClientLevel level, @Nullable LivingEntity entity, int index)
    {
        this.model.update(state, stack, resolver, context, level, entity, index);
    }

    public record Unbaked(ItemModel.Unbaked model, DataObject data) implements ItemModel.Unbaked
    {
        public static final MapCodec<FrameworkItemModel.Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            ItemModels.CODEC.fieldOf("model").forGetter(FrameworkItemModel.Unbaked::model),
            DataObject.CODEC.optionalFieldOf("data", DataObject.EMPTY).forGetter(FrameworkItemModel.Unbaked::data)
        ).apply(builder, FrameworkItemModel.Unbaked::new));

        @Override
        public MapCodec<Unbaked> type()
        {
            return MAP_CODEC;
        }

        @Override
        public ItemModel bake(BakingContext context)
        {
            return new FrameworkItemModel(this.model.bake(context), this.data);
        }

        @Override
        public void resolveDependencies(Resolver resolver)
        {
            this.model.resolveDependencies(resolver);
        }
    }
}
