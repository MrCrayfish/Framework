package com.mrcrayfish.framework.api.client;

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
     * model doesn't exist, however the object will be empty. It should be noted that this gets the
     * data from the default model for the item, you should use {@link #getOpenModelData(ItemStack)}
     * to get ItemStack sensitive version (of which the model could have been changed via the
     * item model component).
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
     * A helper method to access custom data from an Open Model via an ItemStack. Be aware if the model
     * is reloaded and this data is being cached in any way, invalidate it and retrieve the data
     * again. This method returns a DataObject even if the model doesn't have any custom data or the
     * model doesn't exist, however the object will be empty.
     *
     * @param stack an itemstack
     * @return a data object containing the custom data or an empty object
     */
    public static DataObject getOpenModelData(ItemStack stack)
    {
        // Internal code, do not call these directly since they may break in a future update.
        return OpenModelHelper.getData(stack);
    }

    /**
     * Gets the standalone BakedModel for the given ResourceLocation. If no model is found for the given
     * key, the missing model will be returned. If the standalone model uses the Open Model loader,
     * you can cast the model to an {@link com.mrcrayfish.framework.client.model.IOpenModel} to
     * retrieve any custom data.
     *
     * @param location the location of the model
     * @return A baked model or the missing model if not found
     */
    public static BakedModel getStandaloneBakedModel(ResourceLocation location)
    {
        // Internal code, do not call services directly since they may change at any time.
        return ClientServices.CLIENT.getStandaloneBakedModel(location);
    }

    /**
     * Registers a standalone baked model that is not bound to any block or item, and can simply be
     * retrieved after resources have been loaded with the returned supplier. Registration must be
     * done during client initialization. Any calls to this method after the game has started will
     * throw an IllegalStateException. If the standalone model uses the Open Model loader, you can
     * cast the baked model to an {@link com.mrcrayfish.framework.client.model.IOpenModel} to
     * retrieve any custom data.
     *
     * @param location the location of the model in the assets
     * @return A supplier that returns a baked model. Throws IllegalStateException if called too early.
     */
    public static Supplier<BakedModel> registerStandaloneModel(ResourceLocation location)
    {
        // Internal code, do not call these directly since they may change at any time.
        StandaloneModelManager.getInstance().register(location);
        return () -> {
            //noinspection ConstantValue
            if(Minecraft.getInstance().getModelManager().getMissingModel() == null)
                throw new IllegalStateException("Models have not loaded yet");
            // Internal code, do not call services directly since they may change at any time.
            return ClientServices.CLIENT.getStandaloneBakedModel(location);
        };
    }
}
