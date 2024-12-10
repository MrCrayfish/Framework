package com.mrcrayfish.framework.network.message;

import com.mrcrayfish.framework.api.network.ConfigurationMessageContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.function.BiConsumer;

/**
 * Author: MrCrayfish
 */
public class ConfigurationMessage<T> extends FrameworkMessage<T, FriendlyByteBuf, ConfigurationMessageContext>
{
    public static final Marker MARKER = MarkerFactory.getMarker("FRAMEWORK_CONFIGURATION");

    public ConfigurationMessage(CustomPacketPayload.Type<FrameworkPayload<T>> type, Class<T> messageClass, StreamCodec<FriendlyByteBuf, FrameworkPayload<T>> codec, BiConsumer<T, ConfigurationMessageContext> handler, PacketFlow flow)
    {
        super(type, messageClass, codec, handler, flow);
    }
}
