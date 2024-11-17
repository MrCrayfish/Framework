package com.mrcrayfish.framework.client.model.geometry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.math.Transformation;
import com.mrcrayfish.framework.Constants;
import com.mrcrayfish.framework.api.serialize.DataObject;
import com.mrcrayfish.framework.client.model.ForgeBakedOpenModel;
import com.mrcrayfish.framework.client.model.OpenModelDeserializer;
import com.mrcrayfish.framework.util.Utils;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.SimpleUnbakedGeometry;
import net.minecraftforge.client.model.geometry.UnbakedGeometryHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

/**
 * Author: MrCrayfish
 */
public class OpenModelGeometry extends SimpleUnbakedGeometry<OpenModelGeometry>
{
    private final List<BlockElement> elements;
    private final BlockModel model;
    private final DataObject data;

    public OpenModelGeometry(BlockModel model, @Nullable DataObject data)
    {
        this.elements = model.getElements();
        this.model = model;
        this.data = data;
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState)
    {
        return new ForgeBakedOpenModel(super.bake(context, baker, spriteGetter, modelState), this.data);
    }

    @Override
    public void resolveDependencies(UnbakedModel.Resolver modelGetter, IGeometryBakingContext context)
    {
        this.model.resolveDependencies(modelGetter);
    }

    @Override
    protected void addQuads(IGeometryBakingContext owner, IModelBuilder<?> modelBuilder, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState)
    {
        Transformation rootTransform = owner.getRootTransform();
        if(!rootTransform.isIdentity())
            modelState = UnbakedGeometryHelper.composeRootTransformIntoModelState(modelState, rootTransform);
        ModelState finalModelState = modelState;
        this.elements.forEach(element -> {
            element.faces.forEach((side, face) -> {
                TextureAtlasSprite sprite = spriteGetter.apply(owner.getMaterial(face.texture()));
                if(face.cullForDirection() == null) {
                    modelBuilder.addUnculledFace(BlockModel.bakeFace(element, face, sprite, side, finalModelState));
                } else {
                    Direction direction = Direction.rotate(finalModelState.getRotation().getMatrix(), face.cullForDirection());
                    modelBuilder.addCulledFace(direction, BlockModel.bakeFace(element, face, sprite, side, finalModelState));
                }
            });
        });
    }

    @Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class Loader implements IGeometryLoader<OpenModelGeometry>
    {
        @Override
        public OpenModelGeometry read(JsonObject object, JsonDeserializationContext context) throws JsonParseException
        {
            return new OpenModelGeometry(OpenModelDeserializer.INSTANCE.deserialize(object, BlockModel.class, context), DataObject.convertNonNull(object.get("data")));
        }

        @SubscribeEvent
        public static void onModelRegister(ModelEvent.RegisterGeometryLoaders event)
        {
            event.register("open_model", new Loader());
        }
    }
}
