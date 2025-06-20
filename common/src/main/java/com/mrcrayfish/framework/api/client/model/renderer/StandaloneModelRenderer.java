package com.mrcrayfish.framework.api.client.model.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mrcrayfish.framework.api.client.model.FrameworkBakedModel;
import com.mrcrayfish.framework.platform.ClientServices;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.resources.model.QuadCollection;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;

import java.util.List;

public class StandaloneModelRenderer
{
    private static final Direction[] DIRECTIONS = Direction.values();

    /**
     * Draws a FrameworkStandaloneModel into the buffer
     *
     * @param model the model to draw
     * @param stack the current PoseStack
     * @param source a MultiBufferSource instance
     * @param red the amount of red from 0 to 1. Only applicable if model quads are tinted
     * @param green the amount of green from 0 to 1. Only applicable if model quads are tinted
     * @param blue the amount of blue from 0 to 1. Only applicable if model quads are tinted
     * @param light the lighting for the model
     * @param overlay the overlay texture for the model
     */
    public static void draw(FrameworkBakedModel model, PoseStack stack, MultiBufferSource source, float red, float green, float blue, int light, int overlay)
    {
        draw(model.quads(), model.layer(), stack, source, red, green, blue, light, overlay);
    }

    /**
     * Draws a BlockModelPart into the buffer
     *
     * @param model the model to draw
     * @param stack the current PoseStack
     * @param source a MultiBufferSource instance
     * @param red the amount of red from 0 to 1. Only applicable if model quads are tinted
     * @param green the amount of green from 0 to 1. Only applicable if model quads are tinted
     * @param blue the amount of blue from 0 to 1. Only applicable if model quads are tinted
     * @param light the lighting for the model
     * @param overlay the overlay texture for the model
     */
    public static void draw(BlockModelPart model, PoseStack stack, MultiBufferSource source, float red, float green, float blue, int light, int overlay)
    {
        VertexConsumer consumer = source.getBuffer(getSheet(getRenderType(model)));
        for(Direction direction : DIRECTIONS)
        {
            putQuads(stack.last(), consumer, red, green, blue, model.getQuads(direction), light, overlay);
        }
        putQuads(stack.last(), consumer, red, green, blue, model.getQuads(null), light, overlay);
    }

    /**
     * Draws a QuadCollection into the buffer
     *
     * @param collection the model to draw
     * @param layer the chunk section layer of the model
     * @param stack the current PoseStack
     * @param source a MultiBufferSource instance
     * @param red the amount of red from 0 to 1. Only applicable if model quads are tinted
     * @param green the amount of green from 0 to 1. Only applicable if model quads are tinted
     * @param blue the amount of blue from 0 to 1. Only applicable if model quads are tinted
     * @param light the lighting for the model
     * @param overlay the overlay texture for the model
     */
    public static void draw(QuadCollection collection, ChunkSectionLayer layer, PoseStack stack, MultiBufferSource source, float red, float green, float blue, int light, int overlay)
    {
        VertexConsumer consumer = source.getBuffer(getSheet(layer));
        for(Direction direction : DIRECTIONS)
        {
            putQuads(stack.last(), consumer, red, green, blue, collection.getQuads(direction), light, overlay);
        }
        putQuads(stack.last(), consumer, red, green, blue, collection.getQuads(null), light, overlay);
    }

    private static void putQuads(PoseStack.Pose pose, VertexConsumer consumer, float red, float green, float blue, List<BakedQuad> quads, int light, int overlay)
    {
        for(BakedQuad quad : quads)
        {
            if(quad.isTinted())
            {
                red = Mth.clamp(red, 0, 1);
                green = Mth.clamp(green, 0, 1);
                blue = Mth.clamp(blue, 0, 1);
                consumer.putBulkData(pose, quad, red, green, blue, 1, light, overlay);
                return;
            }
            consumer.putBulkData(pose, quad, 1, 1, 1, 1, light, overlay);
        }
    }

    private static ChunkSectionLayer getRenderType(BlockModelPart part)
    {
        return ClientServices.CLIENT.getChunkSectionLayer(part);
    }

    private static RenderType getSheet(ChunkSectionLayer layer)
    {
        return layer == ChunkSectionLayer.TRANSLUCENT ? Sheets.translucentItemSheet() : Sheets.cutoutBlockSheet();
    }
}
