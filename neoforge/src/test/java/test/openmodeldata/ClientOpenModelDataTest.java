package test.openmodeldata;

import com.mrcrayfish.framework.api.client.FrameworkClientAPI;
import com.mrcrayfish.framework.api.serialize.DataNumber;
import com.mrcrayfish.framework.api.serialize.DataObject;
import com.mrcrayfish.framework.api.serialize.DataType;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = "open_model_data_test", bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientOpenModelDataTest
{
    @SubscribeEvent
    private static void onClientSetup(FMLClientSetupEvent event)
    {
        Minecraft.getInstance().getBlockColors().register((state, getter, pos, index) -> {
            DataObject object = FrameworkClientAPI.getOpenModelData(state);
            if(object.has("tint", DataType.NUMBER)) {
                return object.getDataNumber("tint").asInt();
            }
            return 0;
        }, OpenModelDataTest.TEST_BLOCK.get());

        Minecraft.getInstance().getItemColors().register((stack, index) -> {
            DataObject object = FrameworkClientAPI.getOpenModelData(stack.getItem());
            if(object.get("tint") instanceof DataNumber number) {
                return number.asInt();
            }
            return 0;
        }, OpenModelDataTest.TEST_ITEM.get());
    }
}
