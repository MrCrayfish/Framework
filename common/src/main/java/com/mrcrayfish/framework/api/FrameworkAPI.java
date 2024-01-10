package com.mrcrayfish.framework.api;

import com.mrcrayfish.framework.api.data.login.ILoginData;
import com.mrcrayfish.framework.api.network.FrameworkNetwork;
import com.mrcrayfish.framework.api.network.FrameworkNetworkBuilder;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
import com.mrcrayfish.framework.api.sync.SyncedDataKey;
import com.mrcrayfish.framework.entity.sync.SyncedEntityData;
import com.mrcrayfish.framework.network.LoginDataManager;
import com.mrcrayfish.framework.platform.Services;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import org.apache.commons.lang3.function.TriFunction;

import java.util.OptionalInt;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class FrameworkAPI
{
    /**
     * Registers custom login data into Framework's system. This should be called during common setup.
     * An exception will be thrown if this is called after the initialization phase of the game.
     * See {@link ILoginData} for more information.
     *
     * @param id   the id to represent the data
     * @param supplier a supplier returning a login data instance
     */
    public static void registerLoginData(ResourceLocation id, Supplier<ILoginData> supplier)
    {
        // Internal code, do not call these directly since they may break in a future update.
        LoginDataManager.registerLoginData(id, supplier);
    }

    /**
     * Registers a synced data key into Framework's system. This should be called during common setup.
     * An exception will be thrown if this is called after the initialization phase of the game.
     * See {@link SyncedDataKey} for more information.
     *
     * @param key the synced data key instance
     */
    public static <E extends Entity, T> void registerSyncedDataKey(SyncedDataKey<E, T> key)
    {
        // Internal code, do not call these directly since they may break in a future update.
        SyncedEntityData.instance().registerDataKey(key);
    }

    public static FrameworkNetworkBuilder createNetworkBuilder(ResourceLocation id, int version)
    {
        // Internal code, do not call these directly since they may break in a future update.
        return Services.NETWORK.createNetworkBuilder(id, version);
    }

    /**
     * Opens a menu with the ability to provide custom data to clients. The menu type of the menu must
     * be an extended version as registered with {@link RegistryEntry#menuTypeWithData(ResourceLocation, TriFunction)}
     * or an error will occur on the client.
     *
     * @param player   the player opening the menu
     * @param provider the menu provider supplying the menu
     * @param data     the custom data to be sent to the client
     * @return an optional containing the window id or empty if failed to open
     */
    public static OptionalInt openMenuWithData(ServerPlayer player, MenuProvider provider, Consumer<FriendlyByteBuf> data)
    {
        return Services.NETWORK.openMenuWithData(player, provider, data);
    }
}
