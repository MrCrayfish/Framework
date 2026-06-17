package com.mrcrayfish.framework.api.client.model.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.util.ARGB;

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
        collector.submitCustomGeometry(pose, getSheet(model), (pose1, consumer) -> {
            for(Direction direction : DIRECTIONS)
                putQuads(pose1, consumer, red, green, blue, model.getQuads(direction), light, overlay);
            putQuads(pose1, consumer, red, green, blue, model.getQuads(null), light, overlay);
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

    private static RenderType getSheet(BlockStateModelPart part)
    {
        return (part.materialFlags() & 1) != 0 ? Sheets.translucentBlockItemSheet() : Sheets.cutoutBlockItemSheet();
    }
}
