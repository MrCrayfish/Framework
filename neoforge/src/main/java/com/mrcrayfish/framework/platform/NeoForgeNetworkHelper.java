package com.mrcrayfish.framework.platform;

import com.mrcrayfish.framework.api.menu.IMenuData;
import com.mrcrayfish.framework.api.network.FrameworkNetworkBuilder;
import com.mrcrayfish.framework.platform.network.NeoForgeNetworkBuilder;
import com.mrcrayfish.framework.platform.services.INetworkHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;

import java.util.OptionalInt;

/**
 * Author: MrCrayfish
 */
public class NeoForgeNetworkHelper implements INetworkHelper
{
    @Override
    public FrameworkNetworkBuilder createNetworkBuilder(Identifier id, int version)
    {
        return new NeoForgeNetworkBuilder(id, version);
    }

    @Override
    public <D extends IMenuData<D>> OptionalInt openMenuWithData(ServerPlayer player, MenuProvider provider, D data)
    {
        return player.openMenu(provider, buf -> data.codec().encode(buf, data));
    }
}
