package com.mrcrayfish.framework.network.message;

import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.Nullable;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Author: MrCrayfish
 */
public class FrameworkMessage<T>
{
    private final ResourceLocation id;
    private final Class<T> messageClass;
    private final BiConsumer<T, FriendlyByteBuf> encoder;
    private final Function<FriendlyByteBuf, T> decoder;
    private final BiConsumer<T, MessageContext> handler;
    private final @Nullable PacketFlow flow;

    public FrameworkMessage(ResourceLocation id, Class<T> messageClass, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, MessageContext> handler, @Nullable PacketFlow flow)
    {
        this.id = id;
        this.messageClass = messageClass;
        this.encoder = encoder;
        this.decoder = decoder;
        this.handler = handler;
        this.flow = flow;
    }

    public FrameworkPayload<T> readPayload(FriendlyByteBuf buf)
    {
        return new FrameworkPayload<>(this.id, this.decoder.apply(buf), this.encoder);
    }

    public FrameworkPayload<T> writePayload(T msg)
    {
        return new FrameworkPayload<>(this.id, msg, this.encoder);
    }

    public ResourceLocation id()
    {
        return this.id;
    }

    public Class<T> messageClass()
    {
        return this.messageClass;
    }

    public BiConsumer<T, FriendlyByteBuf> encoder()
    {
        return this.encoder;
    }

    public Function<FriendlyByteBuf, T> decoder()
    {
        return this.decoder;
    }

    public BiConsumer<T, MessageContext> handler()
    {
        return this.handler;
    }

    @Nullable
    public PacketFlow flow()
    {
        return this.flow;
    }
}
