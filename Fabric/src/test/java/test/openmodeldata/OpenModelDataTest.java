package test.openmodeldata;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class OpenModelDataTest implements ModInitializer
{
    public static final Block TEST_BLOCK = new Block(BlockBehaviour.Properties.copy(Blocks.GLASS));
    public static final Item TEST_ITEM = new Item(new Item.Properties());

    @Override
    public void onInitialize()
    {
        Registry.register(Registry.BLOCK, new ResourceLocation("framework_test", "test_block"), TEST_BLOCK);
        Registry.register(Registry.ITEM, new ResourceLocation("framework_test", "test_item"), TEST_ITEM);
    }
}
