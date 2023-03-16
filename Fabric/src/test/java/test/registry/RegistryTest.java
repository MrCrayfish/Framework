package test.registry;

import com.mrcrayfish.framework.api.registry.EntryContainer;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

/**
 * Author: MrCrayfish
 */
@EntryContainer
public class RegistryTest
{
    public static final RegistryEntry<Item> THE_BEST_ITEM = RegistryEntry.item(new ResourceLocation("framework_test", "best_item"), () -> new Item(new Item.Properties()));
}
