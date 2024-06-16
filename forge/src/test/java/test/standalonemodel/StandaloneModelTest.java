package test.standalonemodel;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.mrcrayfish.framework.api.client.FrameworkClientAPI;
import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

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

    public static final Supplier<BakedModel> CUSTOM_MODEL = FrameworkClientAPI.registerStandaloneModel(FrameworkClientAPI.createModelResourceLocation(rl("special/custom_model")));
    public static final RegistryEntry<Block> TEST_BLOCK = RegistryEntry.blockWithItem(rl("test"), () -> new TestBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)));
    public static final RegistryEntry<BlockEntityType<TestBlockEntity>> TEST_BLOCK_ENTITY = RegistryEntry.blockEntity(rl("test"), TestBlockEntity::new, () -> new Block[]{TEST_BLOCK.get()});

    public StandaloneModelTest()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::onRegisterRenderers);
    }

    private void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerBlockEntityRenderer(TEST_BLOCK_ENTITY.get(), TestBlockRenderer::new);
    }

    private static class TestBlock extends Block implements EntityBlock
    {
        public TestBlock(Properties properties)
        {
            super(properties);
        }

        @Override
        protected RenderShape getRenderShape(BlockState state)
        {
            return RenderShape.ENTITYBLOCK_ANIMATED;
        }

        @Nullable
        @Override
        public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
        {
            return new TestBlockEntity(pos, state);
        }
    }

    private static class TestBlockEntity extends BlockEntity
    {
        public TestBlockEntity(BlockPos pos, BlockState state)
        {
            super(TEST_BLOCK_ENTITY.get(), pos, state);
        }
    }

    private static class TestBlockRenderer implements BlockEntityRenderer<TestBlockEntity>
    {
        public TestBlockRenderer(BlockEntityRendererProvider.Context context) {}

        @Override
        public void render(TestBlockEntity entity, float partialTick, PoseStack stack, MultiBufferSource source, int light, int overlay)
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
                .renderModel(stack.last(), consumer, TEST_BLOCK.get()
                    .defaultBlockState(), CUSTOM_MODEL.get(), 1.0F, 1.0F, 1.0F, light, overlay);
            stack.popPose();
        }
    }
}
