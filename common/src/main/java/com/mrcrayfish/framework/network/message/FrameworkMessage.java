package com.mrcrayfish.framework.network.message;

import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import org.jetbrains.annotations.Nullable;
import java.util.function.BiConsumer;

/**
 * Author: MrCrayfish
 */
public class FrameworkMessage<T, B extends FriendlyByteBuf, C extends MessageContext>
{
    private final CustomPacketPayload.Type<FrameworkPayload<T>> type;
    private final Class<T> messageClass;
    private final StreamCodec<B, FrameworkPayload<T>> codec;
    private final BiConsumer<T, C> handler;
    private final @Nullable PacketFlow flow;

    public FrameworkMessage(CustomPacketPayload.Type<FrameworkPayload<T>> type, Class<T> messageClass, StreamCodec<B, FrameworkPayload<T>> codec, BiConsumer<T, C> handler, @Nullable PacketFlow flow)
    {
        this.type = type;
        this.messageClass = messageClass;
        this.codec = codec;
        this.handler = handler;
        this.flow = flow;
    }

    /*public FrameworkPayload<T> readPayload(FriendlyByteBuf buf)
    {
        return new FrameworkPayload<>(this.type, this.codec.decode(buf));
    }*/

    public FrameworkPayload<T> writePayload(T msg)
    {
        return new FrameworkPayload<>(this.type, msg);
    }

    public CustomPacketPayload.Type<FrameworkPayload<T>> type()
    {
        return this.type;
    }

    public Class<T> messageClass()
    {
        return this.messageClass;
    }

    public StreamCodec<B, FrameworkPayload<T>> codec()
    {
        return this.codec;
    }

    public BiConsumer<T, C> handler()
    {
        return this.handler;
    }

    @Nullable
    public PacketFlow flow()
    {
        return this.flow;
    }
}
