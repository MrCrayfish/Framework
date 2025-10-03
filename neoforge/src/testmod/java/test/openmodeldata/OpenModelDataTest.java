package test.openmodeldata;

import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.fml.common.Mod;

/**
 * Author: MrCrayfish
 */
@Mod("open_model_data_test")
@RegistryContainer
public class OpenModelDataTest
{
    public static final RegistryEntry<Block> TEST_BLOCK = RegistryEntry.block(ResourceLocation.fromNamespaceAndPath("open_model_data_test", "test_block"), Block::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS));
    public static final RegistryEntry<Item> TEST_ITEM = RegistryEntry.item(ResourceLocation.fromNamespaceAndPath("open_model_data_test", "test_block"), properties -> {
        return new BlockItem(TEST_BLOCK.get(), properties);
    }, Item.Properties::new);
    public static final RegistryEntry<Item> TEST_MODEL = RegistryEntry.item(ResourceLocation.fromNamespaceAndPath("open_model_data_test", "test_model"), Item::new, Item.Properties::new);
}
