package test.openmodeldata;

import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

/**
 * Author: MrCrayfish
 */
@Mod("open_model_data_test")
@RegistryContainer
public class OpenModelDataTest
{
    public static final RegistryEntry<Block> TEST_BLOCK = RegistryEntry.blockWithItem(ResourceLocation.fromNamespaceAndPath("open_model_data_test", "test_block"), () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)));
    public static final RegistryEntry<Item> TEST_ITEM = RegistryEntry.item(ResourceLocation.fromNamespaceAndPath("open_model_data_test", "test_item"), () -> new Item(new Item.Properties()));

    public OpenModelDataTest(IEventBus bus) {}
}
