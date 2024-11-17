package test.openmodel;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import org.jetbrains.annotations.Nullable;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
@Mod("open_model_test")
public class OpenModelTest
{
    private static final DeferredRegister<Block> BLOCK_REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, "open_model_test");
    private static final DeferredRegister<Item> ITEM_REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, "open_model_test");

    private static final RegistryObject<Block> OPEN_MODEL_BLOCK = register("open_model", Block::new, BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS));
    private static final RegistryObject<Block> CHILD_OPEN_MODEL_BLOCK = register("child_open_model", Block::new, BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS));

    public OpenModelTest(FMLJavaModLoadingContext context)
    {
        IEventBus bus = context.getModEventBus();
        BLOCK_REGISTER.register(bus);
        ITEM_REGISTER.register(bus);
    }

    private static <T extends Block> RegistryObject<T> register(String id, Function<BlockBehaviour.Properties, T> blockFunction, BlockBehaviour.Properties properties)
    {
        return register(id, blockFunction, properties, block1 -> new BlockItem(block1, new Item.Properties().setId(ResourceKey.create(Registries.ITEM, rl(id)))));
    }

    private static <T extends Block> RegistryObject<T> register(String id, Function<BlockBehaviour.Properties, T> blockFunction, BlockBehaviour.Properties properties, @Nullable Function<T, BlockItem> supplier)
    {
        RegistryObject<T> registryObject = BLOCK_REGISTER.register(id, () -> blockFunction.apply(properties.setId(ResourceKey.create(Registries.BLOCK, rl(id)))));
        if(supplier != null)
        {
            ITEM_REGISTER.register(id, () -> supplier.apply(registryObject.get()));
        }
        return registryObject;
    }

    private static ResourceLocation rl(String name)
    {
        return ResourceLocation.fromNamespaceAndPath("open_model_test", name);
    }
}
