package test.standalonemodel;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.mrcrayfish.framework.api.client.FrameworkClientAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import java.util.function.Supplier;

@EventBusSubscriber(modid = "standalone_model_test", bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientStandaloneModelTest
{
    private static ResourceLocation rl(String name)
    {
        return ResourceLocation.fromNamespaceAndPath("standalone_model_test", name);
    }

    public static final Supplier<BakedModel> CUSTOM_MODEL = FrameworkClientAPI.registerStandaloneModel(FrameworkClientAPI.createModelResourceLocation(rl("special/custom_model")));

    @SubscribeEvent
    private static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerBlockEntityRenderer(StandaloneModelTest.TEST_BLOCK_ENTITY.get(), TestBlockRenderer::new);
    }

    private static class TestBlockRenderer implements BlockEntityRenderer<StandaloneModelTest.TestBlockEntity>
    {
        public TestBlockRenderer(BlockEntityRendererProvider.Context context) {}

        @Override
        public void render(StandaloneModelTest.TestBlockEntity entity, float partialTick, PoseStack stack, MultiBufferSource source, int light, int overlay)
        {
            stack.pushPose();
            stack.translate(0.5, 0, 0.5);
            stack.mulPose(Axis.YP.rotationDegrees(45));
            stack.scale(2, 2, 2);
            stack.translate(-0.5, 0, -0.5);
            VertexConsumer consumer = source.getBuffer(RenderType.solid());
            Minecraft.getInstance()
                    .getBlockRenderer()
                    .getModelRenderer()
                    .renderModel(stack.last(), consumer, StandaloneModelTest.TEST_BLOCK.get()
                            .defaultBlockState(), CUSTOM_MODEL.get(), 1.0F, 1.0F, 1.0F, light, overlay);
            stack.popPose();
        }
    }
}
