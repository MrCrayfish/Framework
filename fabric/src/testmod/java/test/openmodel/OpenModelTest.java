package test.openmodel;

import com.mrcrayfish.framework.FrameworkSetup;
import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

@RegistryContainer
public class OpenModelTest implements ModInitializer
{
    private static Identifier rl(String name)
    {
        return Identifier.fromNamespaceAndPath("framework_test", name);
    }

    public static final RegistryEntry<Block> OPEN_MODEL_BLOCK = RegistryEntry.blockWithItem(rl("open_model"), Block::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS));
    public static final RegistryEntry<Block> CHILD_OPEN_MODEL_BLOCK = RegistryEntry.block(rl("child_open_model"), Block::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS));

    @Override
    public void onInitialize()
    {
        FrameworkSetup.run();
    }
}
