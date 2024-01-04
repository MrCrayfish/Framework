package com.mrcrayfish.framework.platform;

import com.mrcrayfish.framework.api.network.FrameworkNetworkBuilder;
import com.mrcrayfish.framework.platform.network.FabricNetworkBuilder;
import com.mrcrayfish.framework.platform.services.INetworkHelper;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

import java.util.OptionalInt;
import java.util.function.Consumer;

/**
 * Author: MrCrayfish
 */
public class FabricNetworkHelper implements INetworkHelper
{
    @Override
    public FrameworkNetworkBuilder createNetworkBuilder(ResourceLocation id, int version)
    {
        return new FabricNetworkBuilder(id, version);
    }

    @Override
    public OptionalInt openMenuWithData(ServerPlayer player, MenuProvider provider, Consumer<FriendlyByteBuf> data)
    {
        return player.openMenu(new ExtendedScreenHandlerFactory()
        {
            @Override
            public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buffer)
            {
                data.accept(buffer);
            }

            @Override
            public Component getDisplayName()
            {
                return provider.getDisplayName();
            }

            @Nullable
            @Override
            public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player player)
            {
                return provider.createMenu(windowId, playerInventory, player);
            }
        });
    }
}
