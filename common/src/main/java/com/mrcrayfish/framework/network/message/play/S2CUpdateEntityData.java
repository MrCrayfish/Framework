package com.mrcrayfish.framework.network.message.play;

import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import com.mrcrayfish.framework.client.multiplayer.ClientPlayHandler;
import com.mrcrayfish.framework.entity.sync.DataEntry;
import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class S2CUpdateEntityData extends PlayMessage<S2CUpdateEntityData>
{
    private int entityId;
    private List<DataEntry<?, ?>> entries;

    public S2CUpdateEntityData() {}

    public S2CUpdateEntityData(int entityId, List<DataEntry<?, ?>> entries)
    {
        this.entityId = entityId;
        this.entries = entries;
    }

    @Override
    public void encode(S2CUpdateEntityData message, FriendlyByteBuf buffer)
    {
        buffer.writeVarInt(message.entityId);
        buffer.writeVarInt(message.entries.size());
        message.entries.forEach(entry -> entry.write(buffer));
    }

    @Override
    public S2CUpdateEntityData decode(FriendlyByteBuf buffer)
    {
        int entityId = buffer.readVarInt();
        int size = buffer.readVarInt();
        List<DataEntry<?, ?>> entries = new ArrayList<>();
        for(int i = 0; i < size; i++)
        {
            entries.add(DataEntry.createClientEntry(buffer));
        }
        return new S2CUpdateEntityData(entityId, entries);
    }

    @Override
    public void handle(S2CUpdateEntityData message, MessageContext context)
    {
        context.execute(() -> ClientPlayHandler.handleSyncEntityData(message));
        context.setHandled(true);
    }

    public int getEntityId()
    {
        return this.entityId;
    }

    public List<DataEntry<?, ?>> getEntries()
    {
        return this.entries;
    }
}
