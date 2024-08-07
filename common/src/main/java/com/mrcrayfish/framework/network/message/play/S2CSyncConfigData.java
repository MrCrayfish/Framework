package com.mrcrayfish.framework.network.message.play;

import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import com.mrcrayfish.framework.client.multiplayer.ClientPlayHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public final class S2CSyncConfigData extends PlayMessage<S2CSyncConfigData>
{
    private ResourceLocation id;
    private byte[] data;

    public S2CSyncConfigData() {}

    public S2CSyncConfigData(ResourceLocation id, byte[] data)
    {
        this.id = id;
        this.data = data;
    }

    @Override
    public void encode(S2CSyncConfigData message, FriendlyByteBuf buffer)
    {
        buffer.writeResourceLocation(message.id);
        buffer.writeByteArray(message.data);
    }

    @Override
    public S2CSyncConfigData decode(FriendlyByteBuf buffer)
    {
        return new S2CSyncConfigData(buffer.readResourceLocation(), buffer.readByteArray());
    }

    @Override
    public void handle(S2CSyncConfigData message, MessageContext context)
    {
        context.execute(() -> ClientPlayHandler.handleSyncConfigData(context, message));
        context.setHandled(true);
    }

    public ResourceLocation id()
    {
        return id;
    }

    public byte[] data()
    {
        return data;
    }
}
