package com.mrcrayfish.framework.platform;

import com.mrcrayfish.framework.api.menu.IMenuData;
import com.mrcrayfish.framework.api.network.FrameworkNetworkBuilder;
import com.mrcrayfish.framework.platform.network.ForgeNetworkBuilder;
import com.mrcrayfish.framework.platform.services.INetworkHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.OptionalInt;
import java.util.function.Consumer;

/**
 * Author: MrCrayfish
 */
public class ForgeNetworkHelper implements INetworkHelper
{
    @Override
    public FrameworkNetworkBuilder createNetworkBuilder(ResourceLocation id, int version)
    {
        return new ForgeNetworkBuilder(id, version);
    }

    @Override
    public <D extends IMenuData<D>> OptionalInt openMenuWithData(ServerPlayer player, MenuProvider provider, D data)
    {
        AbstractContainerMenu oldMenu = player.containerMenu;
        player.openMenu(provider, buf -> data.codec().encode(RegistryFriendlyByteBuf.decorator(player.level().registryAccess()).apply(buf), data));
        AbstractContainerMenu newMenu = player.containerMenu;
        return oldMenu != newMenu ? OptionalInt.of(player.containerCounter) : OptionalInt.empty();
    }
}
