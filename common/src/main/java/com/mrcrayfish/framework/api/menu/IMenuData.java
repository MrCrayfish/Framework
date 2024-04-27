package com.mrcrayfish.framework.api.menu;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

/**
 * Author: MrCrayfish
 */
public interface IMenuData<T>
{
    StreamCodec<RegistryFriendlyByteBuf, T> codec();
}
