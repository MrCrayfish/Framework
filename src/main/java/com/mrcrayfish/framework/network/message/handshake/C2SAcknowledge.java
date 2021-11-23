package com.mrcrayfish.framework.network.message.handshake;

import com.mrcrayfish.framework.Framework;
import com.mrcrayfish.framework.api.network.LoginIndexedMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class C2SAcknowledge extends LoginIndexedMessage
{
    public void encode(FriendlyByteBuf buf) {}

    public static C2SAcknowledge decode(FriendlyByteBuf buf)
    {
        return new C2SAcknowledge();
    }

    public static void handle(C2SAcknowledge message, Supplier<NetworkEvent.Context> c)
    {
        Framework.LOGGER.debug(HANDSHAKE, "Received acknowledgement from client");
        c.get().setPacketHandled(true);
    }
}
