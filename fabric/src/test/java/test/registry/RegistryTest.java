package test.registry;

import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
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
    public static final RegistryEntry<Block> THE_BEST_BLOCK = RegistryEntry.block(ResourceLocation.fromNamespaceAndPath("framework_test", "best_item"), () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)));
    public static final RegistryEntry<Item> THE_BEST_ITEM = RegistryEntry.item(ResourceLocation.fromNamespaceAndPath("framework_test", "best_item"), () -> new BlockItem(THE_BEST_BLOCK.get(), new Item.Properties()));
    public static final RegistryEntry<Block> THE_ACTUAL_BEST_BLOCK = RegistryEntry.blockWithItem(ResourceLocation.fromNamespaceAndPath("framework_test", "best_block"), () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)));

    public static final RegistryEntry<CreativeModeTab> CUSTOM_TAB = RegistryEntry.creativeModeTab(ResourceLocation.fromNamespaceAndPath("registry_test", "tab"), builder -> {
        builder.title(Component.literal("Creative tabs are pretty cool!"));
        builder.icon(() -> new ItemStack(Items.STICK));
        builder.displayItems((params, output) -> {
            output.accept(new ItemStack(Items.STICK));
        });
    });
}
