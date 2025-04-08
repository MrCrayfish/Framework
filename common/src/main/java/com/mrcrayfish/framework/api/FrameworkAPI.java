package com.mrcrayfish.framework.api;

import com.mrcrayfish.framework.api.menu.IMenuData;
import com.mrcrayfish.framework.api.network.FrameworkNetworkBuilder;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
import com.mrcrayfish.framework.api.sync.SyncedDataKey;
import com.mrcrayfish.framework.entity.sync.SyncedEntityData;
import com.mrcrayfish.framework.platform.Services;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import org.apache.commons.lang3.function.TriFunction;

import java.util.OptionalInt;
import java.util.function.Consumer;

/**
 * Author: MrCrayfish
 */
public class FrameworkAPI
{
    /**
     * Creates a new platform compatible network builder
     *
     * @param id the id for the network, must be unique
     * @param version the version of the protocol
     * @return a new modloader specific FrameworkNetworkBuilder
     */
    public static FrameworkNetworkBuilder createNetworkBuilder(ResourceLocation id, int version)
    {
        // Internal code, do not call these directly since they may break in a future update.
        return Services.NETWORK.createNetworkBuilder(id, version);
    }

    /**
     * Opens a menu with the ability to provide custom data to clients. The menu type of the menu must
     * be an extended version as registered with {@link RegistryEntry#menuTypeWithData(ResourceLocation, StreamCodec, TriFunction)}
     * or an error will occur on the client.
     *
     * @param player   the player opening the menu
     * @param provider the menu provider supplying the menu
     * @param data     the custom data to be sent to the client
     * @return an optional containing the window id or empty if failed to open
     */
    public static <D extends IMenuData<D>> OptionalInt openMenuWithData(ServerPlayer player, MenuProvider provider, D data)
    {
        // TODO needs to check the data
        return Services.NETWORK.openMenuWithData(player, provider, data);
    }

    /**
     * @return The current environment, either CLIENT or DEDICATED_SERVER
     */
    public static Environment getEnvironment()
    {
        return Services.PLATFORM.getEnvironment();
    }
}
