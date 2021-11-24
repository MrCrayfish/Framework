package com.mrcrayfish.framework.api.network;

import com.mrcrayfish.framework.Framework;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class AcknowledgeMessage extends LoginIndexedMessage
{
    public void encode(FriendlyByteBuf buf) {}

    public static AcknowledgeMessage decode(FriendlyByteBuf buf)
    {
        return new AcknowledgeMessage();
    }

    public static void handle(AcknowledgeMessage message, Supplier<NetworkEvent.Context> c)
    {
        Framework.LOGGER.debug(HANDSHAKE, "Received acknowledgement from client");
        c.get().setPacketHandled(true);
    }
}
