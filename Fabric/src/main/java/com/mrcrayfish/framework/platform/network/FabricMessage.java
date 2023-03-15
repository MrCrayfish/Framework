package com.mrcrayfish.framework.platform.network;

import com.mrcrayfish.framework.api.network.MessageDirection;
import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.network.FriendlyByteBuf;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Author: MrCrayfish
 */
public class FabricMessage<T>
{
    private final int index;
    private final Class<?> messageClass;
    private final BiConsumer<T, FriendlyByteBuf> encoder;
    private final Function<FriendlyByteBuf, T> decoder;
    private final BiConsumer<T, MessageContext> handler;
    @Nullable
    private final MessageDirection direction;

    public FabricMessage(int index, Class<T> messageClass, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, MessageContext> handler, @Nullable MessageDirection direction)
    {
        this.index = index;
        this.messageClass = messageClass;
        this.direction = direction;
        this.encoder = encoder;
        this.decoder = decoder;
        this.handler = handler;
    }

    public int getIndex()
    {
        return this.index;
    }

    public Class<?> getMessageClass()
    {
        return this.messageClass;
    }

    @Nullable
    public MessageDirection getDirection()
    {
        return this.direction;
    }

    public void encode(T message, FriendlyByteBuf buf)
    {
        this.encoder.accept(message, buf);
    }

    public T decode(FriendlyByteBuf buf)
    {
        return this.decoder.apply(buf);
    }

    public void handle(T message, MessageContext context)
    {
        this.handler.accept(message, context);
    }
}
