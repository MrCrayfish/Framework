package test.openmodeldata;

import com.mrcrayfish.framework.api.client.FrameworkClientAPI;
import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
import com.mrcrayfish.framework.api.serialize.DataObject;
import com.mrcrayfish.framework.api.serialize.DataType;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

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
    public static final RegistryEntry<Item> TEST_MODEL = RegistryEntry.item(ResourceLocation.fromNamespaceAndPath("open_model_data_test", "test_model"), Item::new, () -> new Item.Properties());

    public OpenModelDataTest(IEventBus bus)
    {
        bus.addListener(this::onRegisterBlockColors);
        bus.addListener(this::onRegisterItemTintSource);
    }

    private void onRegisterBlockColors(RegisterColorHandlersEvent.Block event)
    {
        event.register((state, getter, pos, index) -> {
            DataObject object = FrameworkClientAPI.getOpenModelData(state);
            if(object.has("tint", DataType.NUMBER)) {
                return object.getDataNumber("tint").asInt();
            }
            return 0xFFFFFF;
        }, TEST_BLOCK.get());
    }

    private void onRegisterItemTintSource(RegisterColorHandlersEvent.ItemTintSources event)
    {
        event.register(TestTintSource.ID, TestTintSource.MAP_CODEC);
    }
}
