package com.mrcrayfish.framework.client.model;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.framework.api.client.model.FrameworkBakedModel;
import com.mrcrayfish.framework.api.serialize.DataObject;
import com.mrcrayfish.framework.util.Utils;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public final class FrameworkItemModel extends BlockModelWrapper implements IOpenModel
{
    public static final ResourceLocation ID = Utils.rl("model");

    private final DataObject data;

    public FrameworkItemModel(List<BakedQuad> quads, List<ItemTintSource> tints, ModelRenderProperties properties, DataObject data)
    {
        super(tints, quads, properties);
        this.data = data;
    }

    @Override
    public DataObject getData()
    {
        return this.data;
    }

    public record Unbaked(ResourceLocation model, List<ItemTintSource> tints) implements ItemModel.Unbaked
    {
        public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            ResourceLocation.CODEC.fieldOf("model").forGetter(Unbaked::model),
            ItemTintSources.CODEC.listOf().optionalFieldOf("tints", List.of()).forGetter(Unbaked::tints)
        ).apply(builder, Unbaked::new));

        @Override
        public MapCodec<Unbaked> type()
        {
            return MAP_CODEC;
        }

        @Override
        public ItemModel bake(BakingContext context)
        {
            ModelBaker baker = context.blockModelBaker();
            ResolvedModel resolvedModel = baker.getModel(this.model);
            TextureSlots textureslots = resolvedModel.getTopTextureSlots();
            FrameworkBakedModel model = FrameworkBakedModel.BAKER.bake(resolvedModel, baker);
            List<BakedQuad> quads = model.quads().getAll();
            ModelRenderProperties properties = ModelRenderProperties.fromResolvedModel(baker, resolvedModel, textureslots);
            return new FrameworkItemModel(quads, this.tints, properties, model.getData());
        }

        @Override
        public void resolveDependencies(Resolver resolver)
        {
            resolver.markDependency(this.model);
        }
    }
}
