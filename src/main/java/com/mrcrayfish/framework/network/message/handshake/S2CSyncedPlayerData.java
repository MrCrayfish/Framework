package com.mrcrayfish.framework.network.message.handshake;

import com.mrcrayfish.framework.Framework;
import com.mrcrayfish.framework.api.data.SyncedDataKey;
import com.mrcrayfish.framework.api.data.SyncedPlayerData;
import com.mrcrayfish.framework.api.network.IMessage;
import com.mrcrayfish.framework.api.network.LoginIndexedMessage;
import com.mrcrayfish.framework.network.Network;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class S2CSyncedPlayerData extends LoginIndexedMessage implements IMessage<S2CSyncedPlayerData>
{
    private final Map<ResourceLocation, Integer> keyMap;

    public S2CSyncedPlayerData()
    {
        this.keyMap = new HashMap<>();
        List<SyncedDataKey<?>> keys = SyncedPlayerData.instance().getKeys();
        keys.forEach(syncedDataKey -> this.keyMap.put(syncedDataKey.getKey(), syncedDataKey.getId()));
    }

    private S2CSyncedPlayerData(Map<ResourceLocation, Integer> keyMap)
    {
        this.keyMap = keyMap;
    }

    @Override
    public void encode(S2CSyncedPlayerData message, FriendlyByteBuf buffer)
    {
        List<SyncedDataKey<?>> keys = SyncedPlayerData.instance().getKeys();
        keys.forEach(syncedDataKey -> {
            buffer.writeResourceLocation(syncedDataKey.getKey());
            buffer.writeVarInt(syncedDataKey.getId());
        });
    }

    @Override
    public S2CSyncedPlayerData decode(FriendlyByteBuf buffer)
    {
        Map<ResourceLocation, Integer> keyMap = new HashMap<>();
        List<SyncedDataKey<?>> keys = SyncedPlayerData.instance().getKeys();
        keys.forEach(syncedDataKey -> keyMap.put(buffer.readResourceLocation(), buffer.readVarInt()));
        return new S2CSyncedPlayerData(keyMap);
    }

    @Override
    public void handle(S2CSyncedPlayerData message, Supplier<NetworkEvent.Context> supplier)
    {
        Framework.LOGGER.debug(HANDSHAKE, "Received synced key mappings from server");
        supplier.get().setPacketHandled(true);
        if(!SyncedPlayerData.instance().updateMappings(message))
        {
            supplier.get().getNetworkManager().disconnect(new TextComponent("Connection closed - [Framework] Received unknown synced data keys. See logs for more details."));
            return;
        }
        Network.getHandshakeChannel().reply(new C2SAcknowledge(), supplier.get());
    }

    public Map<ResourceLocation, Integer> getKeyMap()
    {
        return this.keyMap;
    }
}
