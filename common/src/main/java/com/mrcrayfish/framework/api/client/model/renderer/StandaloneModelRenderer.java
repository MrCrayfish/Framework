package com.mrcrayfish.framework.api.client.model.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mrcrayfish.framework.api.client.model.FrameworkBakedModel;
import com.mrcrayfish.framework.platform.ClientServices;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.core.Direction;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

import java.util.List;

public class StandaloneModelRenderer
{
    private static final Direction[] DIRECTIONS = Direction.values();
    private static final QuadInstance QUAD_INSTANCE = new QuadInstance();

    public static void submitDraw(SubmitNodeCollector collector, BlockStateModelPart model, PoseStack pose, float red, float green, float blue, int light, int overlay)
    {
        submitDraw(collector, model, ChunkSectionLayer.SOLID, pose, red, green, blue, light, overlay);
    }

    public static void submitDraw(SubmitNodeCollector collector, BlockStateModelPart model, ChunkSectionLayer layer, PoseStack pose, float red, float green, float blue, int light, int overlay)
    {
        collector.submitCustomGeometry(pose, getSheet(layer), (pose1, consumer) -> {
            for(Direction direction : DIRECTIONS)
                putQuads(pose1, consumer, red, green, blue, model.getQuads(direction), light, overlay);
            putQuads(pose1, consumer, red, green, blue, model.getQuads(null), light, overlay);
        });
    }

    // changed: MultiBufferSource was removed entirely in MC 26.2 (replaced by the deferred
    // SubmitNodeCollector pipeline) — draw(...) overloads below are now submitDraw(...) overloads
    // taking a SubmitNodeCollector, mirroring the submitDraw(BlockStateModelPart) method above.

    /**
     * Submits a FrameworkStandaloneModel for deferred rendering, usually in a GUI
     *
     * @param collector the SubmitNodeCollector to submit geometry to
     * @param model the model to draw
     * @param stack the current PoseStack
     * @param red the amount of red from 0 to 1. Only applicable if model quads are tinted
     * @param green the amount of green from 0 to 1. Only applicable if model quads are tinted
     * @param blue the amount of blue from 0 to 1. Only applicable if model quads are tinted
     * @param light the lighting for the model
     * @param overlay the overlay texture for the model
     */
    public static void submitDraw(SubmitNodeCollector collector, FrameworkBakedModel model, PoseStack stack, float red, float green, float blue, int light, int overlay)
    {
        submitDraw(collector, model.quads(), model.layer(), stack, red, green, blue, light, overlay);
    }

    /**
     * Submits a QuadCollection for deferred rendering, usually in a GUI
     *
     * @param collector the SubmitNodeCollector to submit geometry to
     * @param collection the model to draw
     * @param layer the chunk section layer of the model
     * @param stack the current PoseStack
     * @param red the amount of red from 0 to 1. Only applicable if model quads are tinted
     * @param green the amount of green from 0 to 1. Only applicable if model quads are tinted
     * @param blue the amount of blue from 0 to 1. Only applicable if model quads are tinted
     * @param light the lighting for the model
     * @param overlay the overlay texture for the model
     */
    public static void submitDraw(SubmitNodeCollector collector, QuadCollection collection, ChunkSectionLayer layer, PoseStack stack, float red, float green, float blue, int light, int overlay)
    {
        float clampedRed = Mth.clamp(red, 0, 1);
        float clampedGreen = Mth.clamp(green, 0, 1);
        float clampedBlue = Mth.clamp(blue, 0, 1);
        collector.submitCustomGeometry(stack, getSheet(layer), (pose1, consumer) -> {
            for(Direction direction : DIRECTIONS)
                putQuads(pose1, consumer, clampedRed, clampedGreen, clampedBlue, collection.getQuads(direction), light, overlay);
            putQuads(pose1, consumer, clampedRed, clampedGreen, clampedBlue, collection.getQuads(null), light, overlay);
        });
    }

    private static void putQuads(PoseStack.Pose pose, VertexConsumer consumer, float red, float green, float blue, List<BakedQuad> quads, int light, int overlay)
    {
        for(BakedQuad quad : quads)
        {
            QUAD_INSTANCE.setLightCoords(light);
            QUAD_INSTANCE.setOverlayCoords(overlay);
            if(quad.materialInfo().isTinted())
            {
                QUAD_INSTANCE.setColor(ARGB.colorFromFloat(1.0F, red, green, blue));
                consumer.putBakedQuad(pose, quad, QUAD_INSTANCE);
                return;
            }
            QUAD_INSTANCE.setColor(0xFFFFFFFF);
            consumer.putBakedQuad(pose, quad, QUAD_INSTANCE);
        }
    }

    private static RenderType getSheet(ChunkSectionLayer layer)
    {
        // changed: Sheets.translucentBlockSheet()/cutoutBlockSheet() renamed with "Item" in MC 26.2
        return layer == ChunkSectionLayer.TRANSLUCENT ? Sheets.translucentBlockItemSheet() : Sheets.cutoutBlockItemSheet();
    }
}
