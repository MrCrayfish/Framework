package test.registry;

import com.mrcrayfish.framework.api.registry.FrameworkRegistry;
import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * Author: MrCrayfish
 */
@RegistryContainer
public class RegistryTest
{
    public static final RegistryEntry<Block> THE_BEST_BLOCK = RegistryEntry.block(Identifier.fromNamespaceAndPath("framework_test", "best_item"), Block::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS));
    public static final RegistryEntry<Item> THE_BEST_ITEM = RegistryEntry.item(Identifier.fromNamespaceAndPath("framework_test", "best_item"), properties -> new BlockItem(THE_BEST_BLOCK.get(), properties), () -> new Item.Properties());
    public static final RegistryEntry<Block> THE_ACTUAL_BEST_BLOCK = RegistryEntry.blockWithItem(Identifier.fromNamespaceAndPath("framework_test", "best_block"), Block::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS));

    public static final FrameworkRegistry<MyCustomObject<?>> REGISTRY = FrameworkRegistry.<MyCustomObject<?>>builder(Identifier.fromNamespaceAndPath("registry_test", "custom_registry")).build();
    public static final RegistryEntry<MyCustomObject<String>> MY_FIRST_CUSTOM_REGISTRY_OBJECT = RegistryEntry.custom(REGISTRY, Identifier.fromNamespaceAndPath("registry_test", "my_awesome_object"), () -> new MyCustomObject<>("Hello World!"));

    public static final RegistryEntry<CreativeModeTab> CUSTOM_TAB = RegistryEntry.creativeModeTab(Identifier.fromNamespaceAndPath("registry_test", "tab"), builder -> {
        builder.title(Component.literal("Creative tabs are pretty cool!"));
        builder.icon(() -> new ItemStack(Items.STICK));
        builder.displayItems((params, output) -> {
            output.accept(new ItemStack(Items.STICK));
        });
    });

    public record MyCustomObject<T>(T value)
    {

    }
}
