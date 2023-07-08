package test.registry;

import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
    public static final RegistryEntry<Block> THE_ACTUAL_BEST_BLOCK = RegistryEntry.blockWithItem(new ResourceLocation("framework_test", "best_block"), () -> new Block(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));

    public static final RegistryEntry<CreativeModeTab> CUSTOM_TAB = RegistryEntry.creativeModeTab(new ResourceLocation("registry_test", "tab"), () -> FabricItemGroup.builder()
            .title(Component.literal("Creative tabs are pretty cool!"))
            .icon(() -> new ItemStack(Items.STICK))
            .displayItems((params, output) -> {
                output.accept(new ItemStack(Items.STICK));
            })
            .build());
}
