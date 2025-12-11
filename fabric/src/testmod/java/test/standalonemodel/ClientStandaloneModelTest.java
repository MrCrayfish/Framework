package test.standalonemodel;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.framework.api.client.model.FrameworkBakedModel;
import com.mrcrayfish.framework.api.client.model.FrameworkModelResource;
import com.mrcrayfish.framework.api.client.model.renderer.StandaloneModelRenderer;
import com.mrcrayfish.framework.api.registry.RegistryContainer;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

@RegistryContainer(clientOnly = true)
public class ClientStandaloneModelTest implements ClientModInitializer
{
    private static Identifier rl(String name)
    {
        return Identifier.fromNamespaceAndPath("framework_test", name);
    }

    //public static final FrameworkModelResource<FrameworkBakedModel> CUSTOM_MODEL = FrameworkModelResource.create(rl("special/custom_model"));

    @Override
    public void onInitializeClient()
    {
        BlockEntityRenderers.register(StandaloneModelTest.TEST_BLOCK_ENTITY.get(), TestBlockRenderer::new);
    }

    private static class TestBlockRenderer implements BlockEntityRenderer<StandaloneModelTest.TestBlockEntity, BlockEntityRenderState>
    {
        public TestBlockRenderer(BlockEntityRendererProvider.Context context) {}

        @Override
        public BlockEntityRenderState createRenderState()
        {
            return new BlockEntityRenderState();
        }

        @Override
        public void submit(BlockEntityRenderState renderState, PoseStack stack, SubmitNodeCollector collector, CameraRenderState cameraState)
        {
            stack.pushPose();
            stack.translate(0.5, 0, 0.5);
            stack.mulPose(Axis.YP.rotationDegrees(45));
            stack.scale(2, 2, 2);
            stack.translate(-0.5, 0, -0.5);
            //StandaloneModelRenderer.submitDraw(collector, CUSTOM_MODEL.getModel(), stack, 1, 1, 1, renderState.lightCoords, OverlayTexture.NO_OVERLAY);
            stack.popPose();
        }
    }
}
