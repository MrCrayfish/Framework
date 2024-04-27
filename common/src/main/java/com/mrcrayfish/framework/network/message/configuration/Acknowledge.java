package com.mrcrayfish.framework.network.message.configuration;

import com.mrcrayfish.framework.Constants;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.network.message.ConfigurationMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.configuration.ServerboundFinishConfigurationPacket;

/**
 * Author: MrCrayfish
 */
public record Acknowledge()
{
    public static final Acknowledge INSTANCE = new Acknowledge();
    public static final StreamCodec<FriendlyByteBuf, Acknowledge> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    public static void handle(Acknowledge message, MessageContext context)
    {
        Constants.LOG.debug(ConfigurationMessage.MARKER, "Received acknowledgement from client");
        context.setHandled(true);
    }
}
