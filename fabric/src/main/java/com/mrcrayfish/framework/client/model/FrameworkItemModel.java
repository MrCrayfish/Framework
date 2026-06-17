package com.mrcrayfish.framework.client.model;

import com.mojang.math.Transformation;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.framework.api.client.model.FrameworkBakedModel;
import com.mrcrayfish.framework.api.serialize.DataObject;
import com.mrcrayfish.framework.util.Utils;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.client.renderer.item.CuboidItemModelWrapper;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4fc;

import java.util.List;
import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public final class FrameworkItemModel extends CuboidItemModelWrapper implements IOpenModel
{
    public static final Identifier ID = Utils.rl("model");

    private final DataObject data;

    public FrameworkItemModel(QuadCollection quads, List<ItemTintSource> tints, ModelRenderProperties properties, Matrix4fc transformation, DataObject data)
    {
        super(tints, quads, properties, transformation);
        this.data = data;
    }

    @Override
    public DataObject getData()
    {
        return this.data;
    }

    public record Unbaked(Identifier model, Optional<Transformation> transformation, List<ItemTintSource> tints) implements ItemModel.Unbaked
    {
        public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            Identifier.CODEC.fieldOf("model").forGetter(Unbaked::model),
            Transformation.EXTENDED_CODEC.optionalFieldOf("transformation").forGetter(Unbaked::transformation),
            ItemTintSources.CODEC.listOf().optionalFieldOf("tints", List.of()).forGetter(Unbaked::tints)
        ).apply(builder, Unbaked::new));

        @Override
        public MapCodec<Unbaked> type()
        {
            return MAP_CODEC;
        }

        @Override
        public ItemModel bake(BakingContext context, Matrix4fc transformation)
        {
            ModelBaker baker = context.blockModelBaker();
            ResolvedModel resolvedModel = baker.getModel(this.model);
            TextureSlots textureSlots = resolvedModel.getTopTextureSlots();
            FrameworkBakedModel model = FrameworkBakedModel.BAKER.bake(resolvedModel, baker);
            QuadCollection quads = model.quads();
            ModelRenderProperties properties = ModelRenderProperties.fromResolvedModel(baker, resolvedModel, textureSlots);
            return new FrameworkItemModel(quads, this.tints, properties, transformation, model.getData());
        }

        @Override
        public void resolveDependencies(Resolver resolver)
        {
            resolver.markDependency(this.model);
        }
    }
}
