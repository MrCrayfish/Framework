package test.standalonemodel;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.framework.api.client.model.FrameworkBakedModel;
import com.mrcrayfish.framework.api.client.model.FrameworkModelResource;
import com.mrcrayfish.framework.api.client.model.renderer.StandaloneModelRenderer;
import com.mrcrayfish.framework.api.registry.RegistryContainer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@RegistryContainer(clientOnly = true)
@Mod(value = "standalone_model_test", dist = Dist.CLIENT)
public class ClientStandaloneModelTest
{
    private static Identifier rl(String name)
    {
        return Identifier.fromNamespaceAndPath("standalone_model_test", name);
    }

    public static final FrameworkModelResource<FrameworkBakedModel> CUSTOM_MODEL = FrameworkModelResource.create(rl("special/custom_model"));

    public ClientStandaloneModelTest(IEventBus bus)
    {
        bus.addListener(this::onRegisterRenderers);
    }

    private void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerBlockEntityRenderer(StandaloneModelTest.TEST_BLOCK_ENTITY.get(), TestBlockRenderer::new);
    }

    public static class TestBlockRenderer implements BlockEntityRenderer<StandaloneModelTest.TestBlockEntity, BlockEntityRenderState>
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
            StandaloneModelRenderer.submitDraw(collector, CUSTOM_MODEL.getModel(), stack, 1, 1, 1, renderState.lightCoords, OverlayTexture.NO_OVERLAY);
            stack.popPose();
        }
    }
}
