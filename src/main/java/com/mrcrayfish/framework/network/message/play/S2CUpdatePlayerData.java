package com.mrcrayfish.framework.network.message.play;

import com.mrcrayfish.framework.common.data.SyncedPlayerData;
import com.mrcrayfish.framework.api.network.IMessage;
import com.mrcrayfish.framework.client.multiplayer.ClientPlayHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class S2CUpdatePlayerData implements IMessage<S2CUpdatePlayerData>
{
    private int entityId;
    private List<SyncedPlayerData.DataEntry<?>> entries;

    private S2CUpdatePlayerData() {}

    public S2CUpdatePlayerData(int entityId, List<SyncedPlayerData.DataEntry<?>> entries)
    {
        this.entityId = entityId;
        this.entries = entries;
    }

    @Override
    public void encode(S2CUpdatePlayerData message, FriendlyByteBuf buffer)
    {
        buffer.writeVarInt(message.entityId);
        buffer.writeVarInt(message.entries.size());
        message.entries.forEach(entry -> entry.write(buffer));
    }

    @Override
    public S2CUpdatePlayerData decode(FriendlyByteBuf buffer)
    {
        int entityId = buffer.readVarInt();
        int size = buffer.readVarInt();
        List<SyncedPlayerData.DataEntry<?>> entries = new ArrayList<>();
        for(int i = 0; i < size; i++)
        {
            entries.add(SyncedPlayerData.DataEntry.read(buffer));
        }
        return new S2CUpdatePlayerData(entityId, entries);
    }

    @Override
    public void handle(S2CUpdatePlayerData message, Supplier<NetworkEvent.Context> supplier)
    {
        supplier.get().enqueueWork(() -> ClientPlayHandler.handleSyncPlayerData(message));
        supplier.get().setPacketHandled(true);
    }

    public int getEntityId()
    {
        return this.entityId;
    }

    public List<SyncedPlayerData.DataEntry<?>> getEntries()
    {
        return this.entries;
    }
}
