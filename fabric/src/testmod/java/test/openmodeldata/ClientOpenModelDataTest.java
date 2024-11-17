package test.openmodeldata;

import com.mrcrayfish.framework.api.client.FrameworkClientAPI;
import com.mrcrayfish.framework.api.serialize.DataHelper;
import com.mrcrayfish.framework.api.serialize.DataNumber;
import com.mrcrayfish.framework.api.serialize.DataObject;
import com.mrcrayfish.framework.api.serialize.DataType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;

/**
 * Author: MrCrayfish
 */
public class ClientOpenModelDataTest implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        ColorProviderRegistry.BLOCK.register((state, getter, pos, index) -> {
            DataObject object = FrameworkClientAPI.getOpenModelData(state);
            return DataHelper.getIntOrDefault(object, "tint", -1);
        }, OpenModelDataTest.TEST_BLOCK.get());

        ColorProviderRegistry.ITEM.register((stack, index) -> {
            DataObject object = FrameworkClientAPI.getOpenModelData(stack.getItem());
            return DataHelper.getIntOrDefault(object, "tint", -1);
        }, OpenModelDataTest.TEST_ITEM.get(), OpenModelDataTest.TEST_BLOCK.get());
    }
}
