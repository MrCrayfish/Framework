package com.mrcrayfish.framework.network.message.handshake;

import com.mrcrayfish.framework.Framework;
import com.mrcrayfish.framework.api.data.sync.SyncedDataKey;
import com.mrcrayfish.framework.api.network.HandshakeMessage;
import com.mrcrayfish.framework.common.data.SyncedEntityData;
import com.mrcrayfish.framework.network.Network;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class S2CSyncedEntityData extends HandshakeMessage<S2CSyncedEntityData>
{
    private Map<ResourceLocation, List<Pair<ResourceLocation, Integer>>> keyMap;

    public S2CSyncedEntityData() {}

    private S2CSyncedEntityData(Map<ResourceLocation, List<Pair<ResourceLocation, Integer>>> keyMap)
    {
        this.keyMap = keyMap;
    }

    @Override
    public void encode(S2CSyncedEntityData message, FriendlyByteBuf buffer)
    {
        Set<SyncedDataKey<?, ?>> keys = SyncedEntityData.instance().getKeys();
        buffer.writeInt(keys.size());
        keys.forEach(key -> {
            int id = SyncedEntityData.instance().getInternalId(key);
            buffer.writeResourceLocation(key.classKey().id());
            buffer.writeResourceLocation(key.id());
            buffer.writeVarInt(id);
        });
    }

    @Override
    public S2CSyncedEntityData decode(FriendlyByteBuf buffer)
    {
        int size = buffer.readInt();
        Map<ResourceLocation, List<Pair<ResourceLocation, Integer>>> keyMap = new HashMap<>();
        for(int i = 0; i < size; i++)
        {
            ResourceLocation classId = buffer.readResourceLocation();
            ResourceLocation keyId = buffer.readResourceLocation();
            int id = buffer.readVarInt();
            keyMap.computeIfAbsent(classId, c -> new ArrayList<>()).add(Pair.of(keyId, id));
        }
        return new S2CSyncedEntityData(keyMap);
    }

    @Override
    public void handle(S2CSyncedEntityData message, Supplier<NetworkEvent.Context> supplier)
    {
        Framework.LOGGER.debug(HANDSHAKE, "Received synced key mappings from server");
        CountDownLatch block = new CountDownLatch(1);
        supplier.get().enqueueWork(() ->
        {
            if(!SyncedEntityData.instance().updateMappings(message))
            {
                supplier.get().getNetworkManager().disconnect(new TextComponent("Connection closed - [Framework] Received unknown synced data keys. See logs for more details."));
            }
            block.countDown();
        });
        try
        {
            block.await();
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }
        supplier.get().setPacketHandled(true);
        Network.getHandshakeChannel().reply(new Acknowledge(), supplier.get());
    }

    public Map<ResourceLocation, List<Pair<ResourceLocation, Integer>>> getKeyMap()
    {
        return this.keyMap;
    }
}
