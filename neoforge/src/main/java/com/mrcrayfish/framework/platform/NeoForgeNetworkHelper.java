package com.mrcrayfish.framework.platform;

import com.mrcrayfish.framework.api.menu.IMenuData;
import com.mrcrayfish.framework.api.network.FrameworkNetworkBuilder;
import com.mrcrayfish.framework.platform.network.NeoForgeNetworkBuilder;
import com.mrcrayfish.framework.platform.services.INetworkHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.OptionalInt;
import java.util.function.Consumer;

/**
 * Author: MrCrayfish
 */
public class NeoForgeNetworkHelper implements INetworkHelper
{
    @Override
    public FrameworkNetworkBuilder createNetworkBuilder(ResourceLocation id, int version)
    {
        return new NeoForgeNetworkBuilder(id, version);
    }

    @Override
    public <D extends IMenuData<D>> OptionalInt openMenuWithData(ServerPlayer player, MenuProvider provider, D data)
    {
        return player.openMenu(provider, buf -> data.codec().encode(buf, data));
    }
}
