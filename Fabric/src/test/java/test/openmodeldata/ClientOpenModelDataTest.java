package test.openmodeldata;

import com.mrcrayfish.framework.api.client.FrameworkClientAPI;
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
        ColorProviderRegistry.BLOCK.register((state, getter, pos, index) ->
        {
            DataObject object = FrameworkClientAPI.getOpenModelData(state);
            if(object.has("tint", DataType.NUMBER))
            {
                return object.getDataNumber("tint").asInt();
            }
            return 0;
        }, OpenModelDataTest.TEST_BLOCK);

        ColorProviderRegistry.ITEM.register((stack, index) ->
        {
            DataObject object = FrameworkClientAPI.getOpenModelData(stack.getItem());
            if(object.get("tint") instanceof DataNumber number)
            {
                return number.asInt();
            }
            return 0;
        }, OpenModelDataTest.TEST_ITEM);
    }
}
