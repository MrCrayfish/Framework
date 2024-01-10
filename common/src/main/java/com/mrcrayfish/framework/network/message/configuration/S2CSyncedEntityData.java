package com.mrcrayfish.framework.network.message.configuration;

import com.mrcrayfish.framework.Constants;
import com.mrcrayfish.framework.api.network.FrameworkResponse;
import com.mrcrayfish.framework.api.sync.SyncedDataKey;
import com.mrcrayfish.framework.entity.sync.SyncedEntityData;
import com.mrcrayfish.framework.network.message.ConfigurationMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

/**
 * Author: MrCrayfish
 */
public record S2CSyncedEntityData(Map<ResourceLocation, List<Pair<ResourceLocation, Integer>>> keyMap)
{
    public static void encode(S2CSyncedEntityData message, FriendlyByteBuf buffer)
    {
        buffer.writeInt(message.keyMap().size());
        message.keyMap().forEach((classId, value) -> {
            buffer.writeResourceLocation(classId);
            buffer.writeVarInt(value.size());
            value.forEach(pair -> {
                buffer.writeResourceLocation(pair.getKey());
                buffer.writeVarInt(pair.getValue());
            });
        });
    }

    public static S2CSyncedEntityData decode(FriendlyByteBuf buffer)
    {
        int keySize = buffer.readInt();
        Map<ResourceLocation, List<Pair<ResourceLocation, Integer>>> keyMap = new HashMap<>();
        for(int i = 0; i < keySize; i++)
        {
            ResourceLocation classId = buffer.readResourceLocation();
            int entrySize = buffer.readVarInt();
            for(int j = 0; j < entrySize; j++)
            {
                ResourceLocation keyId = buffer.readResourceLocation();
                int id = buffer.readVarInt();
                keyMap.computeIfAbsent(classId, c -> new ArrayList<>()).add(Pair.of(keyId, id));
            }
        }
        return new S2CSyncedEntityData(keyMap);
    }

    public static FrameworkResponse handle(S2CSyncedEntityData message, Consumer<Runnable> executor)
    {
        Constants.LOG.debug(ConfigurationMessage.MARKER, "Received synced key mappings from server");
        boolean[] failed = new boolean[1];
        CountDownLatch block = new CountDownLatch(1);
        executor.accept(() -> {
            if(!SyncedEntityData.instance().updateMappings(message)) {
                failed[0] = true;
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
        if(failed[0])
        {
            return FrameworkResponse.error("[Framework] Received unknown synced data keys. See logs for more details.");
        }
        return FrameworkResponse.SUCCESS;
    }

    public Map<ResourceLocation, List<Pair<ResourceLocation, Integer>>> getKeyMap()
    {
        return this.keyMap;
    }
}
