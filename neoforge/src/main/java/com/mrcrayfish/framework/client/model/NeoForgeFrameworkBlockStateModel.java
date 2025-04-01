package com.mrcrayfish.framework.client.model;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.framework.api.serialize.DataObject;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.neoforged.neoforge.client.model.block.CustomUnbakedBlockStateModel;

public class NeoForgeFrameworkBlockStateModel
{
    public record Unbaked(BlockStateModel.Unbaked model, DataObject data) implements CustomUnbakedBlockStateModel
    {
        public static final MapCodec<NeoForgeFrameworkBlockStateModel.Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            BlockStateModel.Unbaked.CODEC.fieldOf("model").forGetter(NeoForgeFrameworkBlockStateModel.Unbaked::model),
            DataObject.CODEC.optionalFieldOf("data", DataObject.EMPTY).forGetter(NeoForgeFrameworkBlockStateModel.Unbaked::data)
        ).apply(builder, NeoForgeFrameworkBlockStateModel.Unbaked::new));

        @Override
        public MapCodec<? extends CustomUnbakedBlockStateModel> codec()
        {
            return MAP_CODEC;
        }

        @Override
        public BlockStateModel bake(ModelBaker baker)
        {
            return new FrameworkBlockStateModel(this.model.bake(baker), this.data);
        }

        @Override
        public void resolveDependencies(Resolver resolver)
        {
            this.model.resolveDependencies(resolver);
        }
    }
}
