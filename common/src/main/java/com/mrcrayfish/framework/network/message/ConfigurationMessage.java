package com.mrcrayfish.framework.network.message;

import com.mrcrayfish.framework.api.network.FrameworkResponse;
import com.mrcrayfish.framework.network.message.configuration.Acknowledge;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Author: MrCrayfish
 */
public class ConfigurationMessage<T> extends FrameworkMessage<T, FriendlyByteBuf>
{
    public static final Marker MARKER = MarkerFactory.getMarker("FRAMEWORK_CONFIGURATION");

    public ConfigurationMessage(CustomPacketPayload.Type<FrameworkPayload<T>> type, Class<T> messageClass, StreamCodec<FriendlyByteBuf, FrameworkPayload<T>> codec, BiFunction<T, Consumer<Runnable>, FrameworkResponse> handler, PacketFlow flow)
    {
        super(type, messageClass, codec, (msg, context) -> {
            FrameworkResponse response = handler.apply(msg, context::execute);
            if(response.isError()) {
                context.disconnect(Component.literal("Connection closed - " + response.message()));
                return;
            }
            context.setHandled(true);
        }, flow);
    }
}
