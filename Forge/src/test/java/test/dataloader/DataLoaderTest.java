package test.dataloader;

import com.google.common.collect.ImmutableList;
import com.mrcrayfish.framework.Framework;
import com.mrcrayfish.framework.api.client.FrameworkClientAPI;
import com.mrcrayfish.framework.api.serialize.DataObject;
import com.mrcrayfish.framework.api.serialize.DataType;
import com.mrcrayfish.framework.client.resources.IDataLoader;
import com.mrcrayfish.framework.client.resources.IResourceSupplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.List;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
@Mod("data_loader_test")
public class DataLoaderTest
{
    public static final Marker MARKER = MarkerManager.getMarker("DATA_LOADER_TEST");

    public DataLoaderTest()
    {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            FrameworkClientAPI.registerDataLoader(new CustomLoader());
        });
    }

    private record ItemResource(Supplier<Item> item) implements IResourceSupplier
    {
        @Override
        public ResourceLocation getLocation()
        {
            ResourceLocation key = ForgeRegistries.ITEMS.getKey(this.item.get());
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
                    Framework.LOGGER.info(MARKER, object.getDataString("message").asString());
                }
            });
        }
    }
}
