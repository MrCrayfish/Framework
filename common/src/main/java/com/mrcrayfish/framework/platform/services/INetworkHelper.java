package com.mrcrayfish.framework.platform.services;

import com.mrcrayfish.framework.api.network.FrameworkNetworkBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;

import java.util.OptionalInt;
import java.util.function.Consumer;

/**
 * Author: MrCrayfish
 */
public interface INetworkHelper
{
    /**
     * Creates a network builder for the respective platform
     *
     * @param id the id of the network
     * @param version the protocol version
     * @return a network builder instance
     */
    FrameworkNetworkBuilder createNetworkBuilder(ResourceLocation id, int version);

    /**
     * Opens a menu with data. This allows custom data to be sent to clients when opening a menu.
     *
     * @param player  the player opening the menu
     * @param provider the menu provider
     * @param data the custom data to be sent to the client
     * @return an optional containing the window id or empty if failed to open
     */
    OptionalInt openMenuWithData(ServerPlayer player, MenuProvider provider, Consumer<FriendlyByteBuf> data);
}
