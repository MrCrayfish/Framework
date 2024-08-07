package com.mrcrayfish.framework.api.network.message;

import com.mrcrayfish.framework.Constants;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.network.message.IMessage;
import com.mrcrayfish.framework.network.message.LoginIndexHolder;
import net.minecraft.network.FriendlyByteBuf;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * Author: MrCrayfish
 */
public abstract class HandshakeMessage<T> extends LoginIndexHolder implements IMessage<T>
{
    public static final Marker HANDSHAKE = MarkerFactory.getMarker("FRAMEWORK_HANDSHAKE");

    public static class Acknowledge extends HandshakeMessage<Acknowledge>
    {
        @Override
        public void encode(Acknowledge message, FriendlyByteBuf buffer) {}

        @Override
        public Acknowledge decode(FriendlyByteBuf buf)
        {
            return new Acknowledge();
        }

        @Override
        public void handle(Acknowledge message, MessageContext context)
        {
            Constants.LOG.debug(HANDSHAKE, "Received acknowledgement from client");
            context.setHandled(true);
        }
    }
}
