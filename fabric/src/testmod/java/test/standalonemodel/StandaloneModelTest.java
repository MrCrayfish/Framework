package test.standalonemodel;

import com.mrcrayfish.framework.FrameworkSetup;
import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Author: MrCrayfish
 */
@RegistryContainer
public class StandaloneModelTest implements ModInitializer
{
    private static Identifier rl(String name)
    {
        return Identifier.fromNamespaceAndPath("framework_test", name);
    }

    public static final RegistryEntry<Block> TEST_BLOCK = RegistryEntry.blockWithItem(rl("standalone_model"), TestBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS));
    public static final RegistryEntry<BlockEntityType<TestBlockEntity>> TEST_BLOCK_ENTITY = RegistryEntry.blockEntity(rl("standalone_model"), TestBlockEntity::new, () -> new Block[]{TEST_BLOCK.get()});

    @Override
    public void onInitialize()
    {
        FrameworkSetup.run();
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
            return RenderShape.INVISIBLE;
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
