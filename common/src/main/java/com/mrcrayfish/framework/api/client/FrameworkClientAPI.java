package com.mrcrayfish.framework.api.client;

import com.mrcrayfish.framework.FrameworkData;
import com.mrcrayfish.framework.api.serialize.DataObject;
import com.mrcrayfish.framework.client.JsonDataManager;
import com.mrcrayfish.framework.client.StandaloneModelManager;
import com.mrcrayfish.framework.client.model.OpenModelHelper;
import com.mrcrayfish.framework.client.resources.IDataLoader;
import com.mrcrayfish.framework.platform.ClientServices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class FrameworkClientAPI
{
    public static synchronized void registerDataLoader(IDataLoader<?> loader)
    {
        JsonDataManager.getInstance().addLoader(loader);
    }

    /**
     * A helper method to access custom data from an Open Model via a Model Resource Location. Be
     * aware if the model is reloaded and this data is being cached in any way, invalidate it and
     * retrieve the data again. This method returns a DataObject even if the model doesn't have any
     * custom data or the model doesn't exist, however the object will be empty.
     *
     * @param location the location of the model
     * @return a data object containing the custom data or an empty object
     */
    public static DataObject getOpenModelData(ModelResourceLocation location)
    {
        // Internal code, do not call these directly since they may break in a future update.
        return OpenModelHelper.getData(location);
    }

    /**
     * A helper method to access custom data from an Open Model via a Block State. Since a block can
     * have different models depending on a block state, this method gets the data from the model
     * tied to the specific block state. Be aware if the model is reloaded and this data is being
     * cached in any way, invalidate it and retrieve the data again. This method returns a
     * DataObject even if the model doesn't have any custom data or the model doesn't exist, however
     * the object will be empty.
     *
     * @param state a block state, which the data is retrieved from the model tied to it
     * @return a data object containing the custom data or an empty object
     */
    public static DataObject getOpenModelData(BlockState state)
    {
        // Internal code, do not call these directly since they may break in a future update.
        return OpenModelHelper.getData(state);
    }

    /**
     * A helper method to access custom data from an Open Model via an Item. Be aware if the model
     * is reloaded and this data is being cached in any way, invalidate it and retrieve the data
     * again. This method returns a DataObject even if the model doesn't have any custom data or the
     * model doesn't exist, however the object will be empty.
     *
     * @param item an item, which the data is retrieved from the model tied to it
     * @return a data object containing the custom data or an empty object
     */
    public static DataObject getOpenModelData(Item item)
    {
        // Internal code, do not call these directly since they may break in a future update.
        return OpenModelHelper.getData(item);
    }

    /**
     * A helper method to access custom data from an Open Model via an ItemStack with support for
     * overrides. Be aware if the model is reloaded and this data is being cached in any way,
     * invalidate it and retrieve the data again. This method returns a DataObject even if the model
     * doesn't have any custom data or the model doesn't exist, however the object will be empty.
     *
     * @param stack an item stack, which the data is retrieved from the model tied to it
     * @param level the level instance or null
     * @param entity the living entity using the stack or null
     * @param seed
     * @return a data object containing the custom data or an empty object
     */
    public static DataObject getOpenModelData(ItemStack stack, @Nullable Level level, @Nullable LivingEntity entity, int seed)
    {
        // Internal code, do not call these directly since they may break in a future update.
        return OpenModelHelper.getData(stack, level, entity, seed);
    }

    /**
     * Gets the BakedModel for the given ModelResourceLocation. If no model is found for the given
     * key, the missing model will be returned. If using a standalone model, make sure your key is
     * created using {@link #createModelResourceLocation(ResourceLocation)} or
     * {@link #createModelResourceLocation(String, String)} to correctly get your model for the
     * specific modloader platform at runtime.
     *
     * @param location the location of the model
     * @return A baked model or the missing model if not found
     */
    public static BakedModel getBakedModel(ModelResourceLocation location)
    {
        // Internal code, do not call services directly since they may change at any time.
        return ClientServices.CLIENT.getBakedModel(location);
    }

    /**
     * Registers a standalone baked model that is not bound to any block or item, and can simply be
     * retrieved after resources have been loaded with the returned supplier. It is important that
     * the ModelResourceLocation is created using {@link #createModelResourceLocation(String, String)}
     * or {@link #createModelResourceLocation(ResourceLocation)} as each modloader platform has a
     * different variant for ModelResourceLocations that mark it as a standalone model. Registration
     * must be done during client initialization. Any calls to this method after the game has started
     * will throw an IllegalStateException.
     *
     * @param location the location of the model in the assets
     * @return A supplier that returns a baked model. Throws IllegalStateException if called too early.
     */
    public static Supplier<BakedModel> registerStandaloneModel(ModelResourceLocation location)
    {
        // Internal code, do not call these directly since they may change at any time.
        StandaloneModelManager.getInstance().register(location);
        return () -> {
            //noinspection ConstantValue
            if(Minecraft.getInstance().getModelManager().getMissingModel() == null)
                throw new IllegalStateException("Models have not loaded yet");
            // Internal code, do not call services directly since they may change at any time.
            return ClientServices.CLIENT.getBakedModel(location);
        };
    }

    /**
     * Creates a platform specific ModelResourceLocation for a standalone model. Since 1.21, modloaders
     * now use different keywords for their ModelResourceLocation variant to load standalone models,
     * so this helps create a platform specific instance during runtime.
     *
     * @param namespace The mod id for searching in specific assets directory
     * @param path      the path of the model relative to the mod id specific directory
     * @return A ModelResourceLocation instance for the specific platform
     */
    public static ModelResourceLocation createModelResourceLocation(String namespace, String path)
    {
        return createModelResourceLocation(ResourceLocation.fromNamespaceAndPath(namespace, path));
    }

    /**
     * Creates a platform specific ModelResourceLocation for a standalone model. Since 1.21, modloaders
     * now use different keywords for their ModelResourceLocation variant to load standalone models,
     * so this helps create a platform specific instance during runtime.
     *
     * @param location The resource location of the mod id and path to the model
     *
     * @return A ModelResourceLocation instance for the specific platform
     */
    public static ModelResourceLocation createModelResourceLocation(ResourceLocation location)
    {
        // Internal code, do not call services directly since they may change at any time.
        String variant = ClientServices.CLIENT.getStandaloneModelVariant();
        return new ModelResourceLocation(location, variant);
    }
}
