package com.mrcrayfish.framework.network.message.configuration;

import com.mrcrayfish.framework.Constants;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.network.message.ConfigurationMessage;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Author: MrCrayfish
 */
public record Acknowledge()
{
    public static void encode(Acknowledge message, FriendlyByteBuf buffer) {}

    public static Acknowledge decode(FriendlyByteBuf buffer)
    {
        return new Acknowledge();
    }

    public static void handle(Acknowledge message, MessageContext context)
    {
        Constants.LOG.debug(ConfigurationMessage.MARKER, "Received acknowledgement from client");
        context.setHandled(true);
    }
}
