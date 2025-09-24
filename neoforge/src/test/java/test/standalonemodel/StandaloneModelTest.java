package test.standalonemodel;

import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
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
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
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

    public static final RegistryEntry<Block> TEST_BLOCK = RegistryEntry.blockWithItem(rl("test"), () -> new TestBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)));
    public static final RegistryEntry<BlockEntityType<TestBlockEntity>> TEST_BLOCK_ENTITY = RegistryEntry.blockEntity(rl("test"), TestBlockEntity::new, () -> new Block[]{TEST_BLOCK.get()});

    public StandaloneModelTest(IEventBus bus) {}

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

    static class TestBlockEntity extends BlockEntity
    {
        public TestBlockEntity(BlockPos pos, BlockState state)
        {
            super(TEST_BLOCK_ENTITY.get(), pos, state);
        }
    }
}
