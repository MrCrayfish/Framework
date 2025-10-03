package test.openmodeldata;

import com.mrcrayfish.framework.api.client.FrameworkClientAPI;
import com.mrcrayfish.framework.api.serialize.DataObject;
import com.mrcrayfish.framework.api.serialize.DataType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = "open_model_data_test")
public class ClientOpenModelDataTest
{
    @SubscribeEvent
    private static void onRegisterBlockColors(RegisterColorHandlersEvent.Block event)
    {
        event.register((state, getter, pos, index) -> {
            DataObject object = FrameworkClientAPI.getOpenModelData(state);
            if(object.has("tint", DataType.NUMBER)) {
                return object.getDataNumber("tint").asInt();
            }
            return 0xFFFFFF;
        }, OpenModelDataTest.TEST_BLOCK.get());
    }

    @SubscribeEvent
    private static void onRegisterItemTintSource(RegisterColorHandlersEvent.ItemTintSources event)
    {
        event.register(TestTintSource.ID, TestTintSource.MAP_CODEC);
    }
}
