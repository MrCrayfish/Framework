package com.mrcrayfish.framework.api.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public interface FrameworkNetworkBuilder
{
    <T> FrameworkNetworkBuilder registerPlayMessage(String name, Class<T> messageClass, StreamCodec<RegistryFriendlyByteBuf, T> codec, BiConsumer<T, MessageContext> handler);

    <T> FrameworkNetworkBuilder registerPlayMessage(String name, Class<T> messageClass, StreamCodec<RegistryFriendlyByteBuf, T> codec, BiConsumer<T, MessageContext> handler, @Nullable PacketFlow flow);

    <T> FrameworkNetworkBuilder registerConfigurationMessage(String name, Class<T> taskClass, StreamCodec<FriendlyByteBuf, T> codec, BiFunction<T, Consumer<Runnable>, FrameworkResponse> handler, Supplier<List<T>> messages);

    <T> FrameworkNetworkBuilder registerConfigurationMessage(String name, Class<T> taskClass, StreamCodec<FriendlyByteBuf, T> codec, BiFunction<T, Consumer<Runnable>, FrameworkResponse> handler, Supplier<List<T>> messages, @Nullable PacketFlow flow);

    FrameworkNetworkBuilder optional();

    FrameworkNetwork build();

    static ResourceLocation createMessageId(ResourceLocation id, String name)
    {
        return new ResourceLocation(id.getNamespace(), id.getPath() + "/" + name);
    }
}
