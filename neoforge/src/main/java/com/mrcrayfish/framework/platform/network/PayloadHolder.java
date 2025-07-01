package com.mrcrayfish.framework.platform.network;

import com.mrcrayfish.framework.network.message.FrameworkPayload;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import org.jetbrains.annotations.Nullable;

public record PayloadHolder<T, R extends FriendlyByteBuf>(@Nullable PacketFlow flow, CustomPacketPayload.Type<FrameworkPayload<T>> type, StreamCodec<R, FrameworkPayload<T>> codec, IPayloadHandler<FrameworkPayload<T>> handler)
{
}
