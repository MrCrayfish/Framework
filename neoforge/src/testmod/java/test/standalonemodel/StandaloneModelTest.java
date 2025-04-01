package test.standalonemodel;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.framework.api.client.FrameworkClientAPI;
import com.mrcrayfish.framework.api.client.model.FrameworkBakedModel;
import com.mrcrayfish.framework.api.client.model.FrameworkModelResource;
import com.mrcrayfish.framework.api.client.model.renderer.StandaloneModelRenderer;
import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.InitializeClientRegistriesEvent;
import org.jetbrains.annotations.Nullable;

/**
 * Author: MrCrayfish
 */
@Mod("standalone_model_test")
@RegistryContainer
public class StandaloneModelTest
{
    private static ResourceLocation rl(String name)
    {
        return ResourceLocation.fromNamespaceAndPath("standalone_model_test", name);
    }

    public static final FrameworkModelResource<FrameworkBakedModel> CUSTOM_MODEL = FrameworkModelResource.create(rl("special/custom_model"));
    public static final RegistryEntry<Block> TEST_BLOCK = RegistryEntry.blockWithItem(rl("test"), TestBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS));
    public static final RegistryEntry<BlockEntityType<TestBlockEntity>> TEST_BLOCK_ENTITY = RegistryEntry.blockEntity(rl("test"), TestBlockEntity::new, () -> new Block[]{TEST_BLOCK.get()});

    public StandaloneModelTest(IEventBus bus)
    {
        bus.addListener(this::onRegisterRenderers);
        bus.addListener(this::onClientSetup);
    }

    private void onClientSetup(InitializeClientRegistriesEvent event)
    {
        FrameworkClientAPI.registerStandaloneModel(CUSTOM_MODEL);
    }

    private void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerBlockEntityRenderer(TEST_BLOCK_ENTITY.get(), TestBlockRenderer::new);
    }

    public static class TestBlock extends Block implements EntityBlock
    {
        public TestBlock(Properties properties)
        {
            super(properties);
        }

        @Nullable
        @Override
        public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
        {
            return new TestBlockEntity(pos, state);
        }
    }

    public static class TestBlockEntity extends BlockEntity
    {
        public TestBlockEntity(BlockPos pos, BlockState state)
        {
            super(TEST_BLOCK_ENTITY.get(), pos, state);
        }
    }

    public static class TestBlockRenderer implements BlockEntityRenderer<TestBlockEntity>
    {
        public TestBlockRenderer(BlockEntityRendererProvider.Context context) {}

        @Override
        public void render(TestBlockEntity entity, float partialTick, PoseStack stack, MultiBufferSource source, int light, int overlay, Vec3 camera)
        {
            stack.pushPose();
            stack.translate(0.5, 0, 0.5);
            stack.mulPose(Axis.YP.rotationDegrees(45));
            stack.scale(2, 2, 2);
            stack.translate(-0.5, 0, -0.5);
            StandaloneModelRenderer.draw(CUSTOM_MODEL.getModel(), stack, source, 1, 1, 1, light, overlay);
            stack.popPose();
        }
    }
}
