package com.mrcrayfish.framework.api.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public interface FrameworkNetworkBuilder
{
    <T> FrameworkNetworkBuilder registerPlayMessage(String name, Class<T> messageClass, StreamCodec<RegistryFriendlyByteBuf, T> codec, BiConsumer<T, PlayMessageContext> handler);

    <T> FrameworkNetworkBuilder registerPlayMessage(String name, Class<T> messageClass, StreamCodec<RegistryFriendlyByteBuf, T> codec, BiConsumer<T, PlayMessageContext> handler, @Nullable PacketFlow flow);

    <T> FrameworkNetworkBuilder registerConfigurationMessage(String name, Class<T> taskClass, StreamCodec<FriendlyByteBuf, T> codec, BiConsumer<T, ConfigurationMessageContext> handler, Supplier<List<T>> messages);

    <T> FrameworkNetworkBuilder registerConfigurationMessage(String name, Class<T> taskClass, StreamCodec<FriendlyByteBuf, T> codec, BiConsumer<T, ConfigurationMessageContext> handler, Supplier<List<T>> messages, @Nullable PacketFlow flow);

    <T> FrameworkNetworkBuilder registerConfigurationMessage(String name, Class<T> taskClass, StreamCodec<FriendlyByteBuf, T> codec, BiConsumer<T, ConfigurationMessageContext> handler, Supplier<List<T>> messages, @Nullable PacketFlow flow, boolean completeImmediately);

    FrameworkNetworkBuilder optional();

    FrameworkNetwork build();

    static ResourceLocation createMessageId(ResourceLocation id, String name)
    {
        return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), id.getPath() + "/" + name);
    }
}
