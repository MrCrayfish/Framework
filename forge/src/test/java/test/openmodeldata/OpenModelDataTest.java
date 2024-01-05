package test.openmodeldata;

import com.mrcrayfish.framework.api.client.FrameworkClientAPI;
import com.mrcrayfish.framework.api.serialize.DataNumber;
import com.mrcrayfish.framework.api.serialize.DataObject;
import com.mrcrayfish.framework.api.serialize.DataType;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
@Mod("open_model_data_test")
public class OpenModelDataTest
{
    private static final DeferredRegister<Block> BLOCK_REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, "open_model_data_test");
    private static final DeferredRegister<Item> ITEM_REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, "open_model_data_test");

    private static final RegistryObject<Block> TEST_BLOCK = register("test_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)));
    private static final RegistryObject<Item> TEST_ITEM = ITEM_REGISTER.register("test_item", () -> new Item(new Item.Properties()));

    public OpenModelDataTest()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCK_REGISTER.register(bus);
        ITEM_REGISTER.register(bus);
        bus.addListener(this::onClientSetup);
    }

    private void onClientSetup(FMLClientSetupEvent event)
    {
        Minecraft.getInstance().getBlockColors().register((state, getter, pos, index) ->
        {
            DataObject object = FrameworkClientAPI.getOpenModelData(state);
            if(object.has("tint", DataType.NUMBER))
            {
                return object.getDataNumber("tint").asInt();
            }
            return 0;
        }, TEST_BLOCK.get());

        Minecraft.getInstance().getItemColors().register((stack, index) ->
        {
            DataObject object = FrameworkClientAPI.getOpenModelData(stack.getItem());
            if(object.get("tint") instanceof DataNumber number)
            {
                return number.asInt();
            }
            return 0;
        }, TEST_ITEM.get());
    }

    private static <T extends Block> RegistryObject<T> register(String id, Supplier<T> block)
    {
        return register(id, block, block1 -> new BlockItem(block1, new Item.Properties()));
    }

    private static <T extends Block> RegistryObject<T> register(String id, Supplier<T> block, @Nullable Function<T, BlockItem> supplier)
    {
        RegistryObject<T> registryObject = BLOCK_REGISTER.register(id, block);
        if(supplier != null)
        {
            ITEM_REGISTER.register(id, () -> supplier.apply(registryObject.get()));
        }
        return registryObject;
    }
}
