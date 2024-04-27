package com.mrcrayfish.framework.network.message;

import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

/**
 * Author: MrCrayfish
 */
public class PlayMessage<T> extends FrameworkMessage<T, RegistryFriendlyByteBuf>
{
    public PlayMessage(CustomPacketPayload.Type<FrameworkPayload<T>> type, Class<T> messageClass, StreamCodec<RegistryFriendlyByteBuf, FrameworkPayload<T>> codec, BiConsumer<T, MessageContext> handler, @Nullable PacketFlow flow)
    {
        super(type, messageClass, codec, handler, flow);
    }
}
