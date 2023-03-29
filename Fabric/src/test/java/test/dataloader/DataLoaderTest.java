package test.dataloader;

import com.google.common.collect.ImmutableList;
import com.mrcrayfish.framework.Constants;
import com.mrcrayfish.framework.api.client.FrameworkClientAPI;
import com.mrcrayfish.framework.api.serialize.DataObject;
import com.mrcrayfish.framework.api.serialize.DataType;
import com.mrcrayfish.framework.client.resources.IDataLoader;
import com.mrcrayfish.framework.client.resources.IResourceSupplier;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.List;
import java.util.function.Supplier;

public class DataLoaderTest implements ClientModInitializer
{
    public static final Marker MARKER = MarkerFactory.getMarker("DATA_LOADER_TEST");

    @Override
    public void onInitializeClient()
    {
        FrameworkClientAPI.registerDataLoader(new CustomLoader());
    }

    private record ItemResource(Supplier<Item> item) implements IResourceSupplier
    {
        @Override
        public ResourceLocation getLocation()
        {
            ResourceLocation key = Registry.ITEM.getKey(this.item.get());
            return new ResourceLocation(key.getNamespace(), "custom_data/" + key.getPath() + ".json");
        }
    }

    private static class CustomLoader implements IDataLoader<ItemResource>
    {
        @Override
        public List<ItemResource> getResourceSuppliers()
        {
            return ImmutableList.of(new ItemResource(() -> Items.BEEF));
        }

        @Override
        @SuppressWarnings("ConstantConditions")
        public void process(List<Pair<ItemResource, DataObject>> results)
        {
            results.forEach(pair ->
            {
                DataObject object = pair.getRight();
                if(object.has("message", DataType.STRING))
                {
                    Constants.LOG.info(MARKER, object.getDataString("message").asString());
                }
            });
        }
    }
}
