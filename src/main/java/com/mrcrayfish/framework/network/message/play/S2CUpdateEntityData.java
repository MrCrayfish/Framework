package com.mrcrayfish.framework.network.message.play;

import com.mrcrayfish.framework.api.network.PlayMessage;
import com.mrcrayfish.framework.client.multiplayer.ClientPlayHandler;
import com.mrcrayfish.framework.common.data.SyncedEntityData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class S2CUpdateEntityData extends PlayMessage<S2CUpdateEntityData>
{
    private int entityId;
    private List<SyncedEntityData.DataEntry<?, ?>> entries;

    public S2CUpdateEntityData() {}

    public S2CUpdateEntityData(int entityId, List<SyncedEntityData.DataEntry<?, ?>> entries)
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
        List<SyncedEntityData.DataEntry<?, ?>> entries = new ArrayList<>();
        for(int i = 0; i < size; i++)
        {
            entries.add(SyncedEntityData.DataEntry.read(buffer));
        }
        return new S2CUpdateEntityData(entityId, entries);
    }

    @Override
    public void handle(S2CUpdateEntityData message, Supplier<NetworkEvent.Context> supplier)
    {
        supplier.get().enqueueWork(() -> ClientPlayHandler.handleSyncEntityData(message));
        supplier.get().setPacketHandled(true);
    }

    public int getEntityId()
    {
        return this.entityId;
    }

    public List<SyncedEntityData.DataEntry<?, ?>> getEntries()
    {
        return this.entries;
    }
}
