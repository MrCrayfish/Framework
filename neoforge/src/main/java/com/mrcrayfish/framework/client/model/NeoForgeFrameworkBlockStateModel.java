package com.mrcrayfish.framework.client.model;

import com.mojang.serialization.MapCodec;
import com.mrcrayfish.framework.api.client.model.FrameworkBakedModel;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvedModel;
import net.neoforged.neoforge.client.model.block.CustomUnbakedBlockStateModel;

public class NeoForgeFrameworkBlockStateModel
{
    public record Unbaked(Variant variant) implements CustomUnbakedBlockStateModel
    {
        public static final MapCodec<NeoForgeFrameworkBlockStateModel.Unbaked> MAP_CODEC = Variant.MAP_CODEC.xmap(NeoForgeFrameworkBlockStateModel.Unbaked::new, NeoForgeFrameworkBlockStateModel.Unbaked::variant);

        @Override
        public MapCodec<? extends CustomUnbakedBlockStateModel> codec()
        {
            return MAP_CODEC;
        }

        @Override
        public BlockStateModel bake(ModelBaker baker)
        {
            ResolvedModel resolvedModel = baker.getModel(this.variant.modelLocation());
            FrameworkBakedModel model = FrameworkBakedModel.BAKER.bake(resolvedModel, baker);
            return new FrameworkBlockStateModel(model);
        }

        @Override
        public void resolveDependencies(Resolver resolver)
        {
            this.variant.resolveDependencies(resolver);
        }
    }
}
