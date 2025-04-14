package test.openmodeldata;

import com.mrcrayfish.framework.api.client.FrameworkClientAPI;
import com.mrcrayfish.framework.api.serialize.DataHelper;
import com.mrcrayfish.framework.api.serialize.DataObject;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.resources.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public class ClientOpenModelDataTest implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        ItemTintSources.ID_MAPPER.put(ResourceLocation.fromNamespaceAndPath("framework_test", "tint"), TestTintSource.MAP_CODEC);
        ColorProviderRegistry.BLOCK.register((state, getter, pos, index) -> {
            DataObject object = FrameworkClientAPI.getOpenModelData(state);
            return DataHelper.getIntOrDefault(object, "tint", -1);
        }, OpenModelDataTest.TEST_BLOCK.get());
    }
}
