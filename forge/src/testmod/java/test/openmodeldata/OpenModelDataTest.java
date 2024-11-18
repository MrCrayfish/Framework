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
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Author: MrCrayfish
 */
@Mod("open_model_data_test")
@RegistryContainer
public class OpenModelDataTest
{
    public static final RegistryEntry<Block> TEST_BLOCK = RegistryEntry.blockWithItem(rl("test_block"), Block::new, BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS));
    public static final RegistryEntry<Item> TEST_ITEM = RegistryEntry.item(rl("test_item"), Item::new, new Item.Properties());

    public OpenModelDataTest(FMLJavaModLoadingContext context)
    {
        IEventBus bus = context.getModEventBus();
        bus.addListener(this::onRegisterBlockColors);
        bus.addListener(this::onRegisterItemColors);
    }

    private void onRegisterBlockColors(RegisterColorHandlersEvent.Block event)
    {
        event.register((state, getter, pos, index) -> {
            DataObject object = FrameworkClientAPI.getOpenModelData(state);
            if(object.has("tint", DataType.NUMBER)) {
                return object.getDataNumber("tint").asInt();
            }
            return -1;
        }, TEST_BLOCK.get());
    }

    private void onRegisterItemColors(RegisterColorHandlersEvent.Item event)
    {
        event.register((stack, index) -> {
            DataObject object = FrameworkClientAPI.getOpenModelData(stack.getItem());
            if(object.get("tint") instanceof DataNumber number) {
                return number.asInt();
            }
            return -1;
        }, TEST_ITEM.get(), TEST_BLOCK.get());
    }

    private static ResourceLocation rl(String name)
    {
        return ResourceLocation.fromNamespaceAndPath("open_model_data_test", name);
    }
}
