package com.mrcrayfish.framework.api.client;

import com.mrcrayfish.framework.api.serialize.DataObject;
import com.mrcrayfish.framework.client.JsonDataManager;
import com.mrcrayfish.framework.client.model.OpenModelHelper;
import com.mrcrayfish.framework.client.resources.IDataLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

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
     * A helper method to access custom data from an Open Model via a Resource Location. Be
     * aware if the model is reloaded and this data is being cached in any way, invalidate it and
     * retrieve the data again. This method returns a DataObject even if the model doesn't have any
     * custom data or the model doesn't exist, however the object will be empty.
     *
     * @param modelLocation the location of the model
     * @return a data object containing the custom data or an empty object
     */
    public static DataObject getOpenModelData(ResourceLocation modelLocation)
    {
        // Internal code, do not call these directly since they may break in a future update.
        return OpenModelHelper.getData(modelLocation);
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
}
