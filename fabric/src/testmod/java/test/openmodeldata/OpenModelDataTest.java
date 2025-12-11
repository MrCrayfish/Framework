package test.openmodeldata;

import com.mrcrayfish.framework.FrameworkSetup;
import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

@RegistryContainer
public class OpenModelDataTest implements ModInitializer
{
    private static Identifier rl(String name)
    {
        return Identifier.fromNamespaceAndPath("framework_test", name);
    }

    public static final RegistryEntry<Block> TEST_BLOCK = RegistryEntry.blockWithItem(rl("test_block"), Block::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS));
    public static final RegistryEntry<Item> TEST_ITEM = RegistryEntry.item(rl("test_model"), Item::new, () -> new Item.Properties());

    @Override
    public void onInitialize()
    {
        FrameworkSetup.run();
    }
}
