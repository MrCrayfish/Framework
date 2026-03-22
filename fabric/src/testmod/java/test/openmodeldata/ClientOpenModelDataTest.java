package test.openmodeldata;

import com.mrcrayfish.framework.api.client.FrameworkClientAPI;
import com.mrcrayfish.framework.api.serialize.DataHelper;
import com.mrcrayfish.framework.api.serialize.DataObject;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorResolverRegistry;
import net.fabricmc.fabric.impl.client.rendering.BlockColorRegistryImpl;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.resources.Identifier;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public class ClientOpenModelDataTest implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        ItemTintSources.ID_MAPPER.put(Identifier.fromNamespaceAndPath("framework_test", "tint"), TestTintSource.MAP_CODEC);
        BlockColorRegistry.register(List.of(state -> {
            DataObject object = FrameworkClientAPI.getOpenModelData(state);
            return DataHelper.getIntOrDefault(object, "tint", -1);
        }), OpenModelDataTest.TEST_BLOCK.get());
    }
}
