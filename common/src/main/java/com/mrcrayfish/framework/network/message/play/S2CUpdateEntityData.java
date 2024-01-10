package com.mrcrayfish.framework.network.message.play;

import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.client.multiplayer.ClientPlayHandler;
import com.mrcrayfish.framework.entity.sync.DataEntry;
import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public record S2CUpdateEntityData(int entityId, List<DataEntry<?, ?>> entries)
{
    public static void encode(S2CUpdateEntityData message, FriendlyByteBuf buffer)
    {
        buffer.writeVarInt(message.entityId);
        buffer.writeVarInt(message.entries.size());
        message.entries.forEach(entry -> entry.write(buffer));
    }

    public static S2CUpdateEntityData decode(FriendlyByteBuf buffer)
    {
        int entityId = buffer.readVarInt();
        int size = buffer.readVarInt();
        List<DataEntry<?, ?>> entries = new ArrayList<>();
        for(int i = 0; i < size; i++)
        {
            entries.add(DataEntry.read(buffer));
        }
        return new S2CUpdateEntityData(entityId, entries);
    }

    public static void handle(S2CUpdateEntityData message, MessageContext context)
    {
        context.execute(() -> ClientPlayHandler.handleSyncEntityData(message));
        context.setHandled(true);
    }
}
