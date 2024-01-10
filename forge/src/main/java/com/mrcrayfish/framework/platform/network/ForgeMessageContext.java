package com.mrcrayfish.framework.platform.network;

import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraftforge.event.network.CustomPayloadEvent;

/**
 * Author: MrCrayfish
 */
public class ForgeMessageContext extends MessageContext
{
    private final CustomPayloadEvent.Context context;

    public ForgeMessageContext(CustomPayloadEvent.Context context, PacketFlow flow)
    {
        super(flow, context.getSender());
        this.context = context;
    }

    @Override
    public void execute(Runnable runnable)
    {
        this.context.enqueueWork(runnable);
    }

    @Override
    public void disconnect(Component reason)
    {
        this.context.getConnection().disconnect(reason);
    }

    @Override
    public void setHandled(boolean handled)
    {
        this.context.setPacketHandled(handled);
    }
}
