package test.openmodel;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class OpenModelTest implements ModInitializer
{
    private static final Block OPEN_MODEL_BLOCK = new Block(BlockBehaviour.Properties.copy(Blocks.GLASS));
    private static final Block CHILD_OPEN_MODEL_BLOCK = new Block(BlockBehaviour.Properties.copy(Blocks.GLASS));

    @Override
    public void onInitialize()
    {
        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation("framework_test", "open_model"), OPEN_MODEL_BLOCK);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("framework_test", "open_model"), new BlockItem(OPEN_MODEL_BLOCK, new Item.Properties()));
        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation("framework_test", "child_open_model"), CHILD_OPEN_MODEL_BLOCK);
    }
}
