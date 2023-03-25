package test.registry;

import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * Author: MrCrayfish
 */
@RegistryContainer
public class RegistryTest
{
    public static final RegistryEntry<Block> THE_BEST_BLOCK = RegistryEntry.block(new ResourceLocation("framework_test", "best_item"), () -> new Block(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));
    public static final RegistryEntry<Item> THE_BEST_ITEM = RegistryEntry.item(new ResourceLocation("framework_test", "best_item"), () -> new BlockItem(THE_BEST_BLOCK.get(), new Item.Properties()));
}
