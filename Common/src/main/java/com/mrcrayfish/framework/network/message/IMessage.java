package com.mrcrayfish.framework.network.message;

import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Author: MrCrayfish
 */
public interface IMessage<T>
{
    void encode(T message, FriendlyByteBuf buffer);

    T decode(FriendlyByteBuf buffer);

    void handle(T message, MessageContext context);
}