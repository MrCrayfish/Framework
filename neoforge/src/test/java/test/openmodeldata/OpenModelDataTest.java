package test.openmodeldata;

import com.mrcrayfish.framework.api.client.FrameworkClientAPI;
import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
import com.mrcrayfish.framework.api.serialize.DataNumber;
import com.mrcrayfish.framework.api.serialize.DataObject;
import com.mrcrayfish.framework.api.serialize.DataType;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * Author: MrCrayfish
 */
@Mod("open_model_data_test")
@RegistryContainer
public class OpenModelDataTest
{
    public static final RegistryEntry<Block> TEST_BLOCK = RegistryEntry.blockWithItem(ResourceLocation.fromNamespaceAndPath("open_model_data_test", "test_block"), () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)));
    public static final RegistryEntry<Item> TEST_ITEM = RegistryEntry.item(ResourceLocation.fromNamespaceAndPath("open_model_data_test", "test_item"), () -> new Item(new Item.Properties()));

    public OpenModelDataTest(IEventBus bus)
    {
        bus.addListener(this::onClientSetup);
    }

    private void onClientSetup(FMLClientSetupEvent event)
    {
        Minecraft.getInstance().getBlockColors().register((state, getter, pos, index) -> {
            DataObject object = FrameworkClientAPI.getOpenModelData(state);
            if(object.has("tint", DataType.NUMBER)) {
                return object.getDataNumber("tint").asInt();
            }
            return 0;
        }, TEST_BLOCK.get());

        Minecraft.getInstance().getItemColors().register((stack, index) -> {
            DataObject object = FrameworkClientAPI.getOpenModelData(stack.getItem());
            if(object.get("tint") instanceof DataNumber number) {
                return number.asInt();
            }
            return 0;
        }, TEST_ITEM.get());
    }
}
