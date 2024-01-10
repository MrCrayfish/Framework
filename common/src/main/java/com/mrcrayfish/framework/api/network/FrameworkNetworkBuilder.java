package com.mrcrayfish.framework.api.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;

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
    <T> FrameworkNetworkBuilder registerPlayMessage(String name, Class<T> messageClass, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, MessageContext> handler);

    <T> FrameworkNetworkBuilder registerPlayMessage(String name, Class<T> messageClass, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, MessageContext> handler, @Nullable PacketFlow flow);

    <T> FrameworkNetworkBuilder registerConfigurationMessage(String name, Class<T> taskClass, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiFunction<T, Consumer<Runnable>, FrameworkResponse> handler, Supplier<List<T>> messages);

    FrameworkNetworkBuilder optional();

    FrameworkNetwork build();
}
