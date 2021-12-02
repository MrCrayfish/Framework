package com.mrcrayfish.framework.api.network;

import com.mrcrayfish.framework.Framework;
import com.mrcrayfish.framework.network.message.IMessage;
import com.mrcrayfish.framework.network.message.handshake.LoginIndexHolder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public abstract class HandshakeMessage<T> extends LoginIndexHolder implements IMessage<T>
{
    public static final Marker HANDSHAKE = MarkerManager.getMarker("FRAMEWORK_HANDSHAKE");

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
        public void handle(Acknowledge message, Supplier<NetworkEvent.Context> c)
        {
            Framework.LOGGER.debug(HANDSHAKE, "Received acknowledgement from client");
            c.get().setPacketHandled(true);
        }
    }
}
