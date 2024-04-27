package com.mrcrayfish.framework.network.message.configuration;

import com.mrcrayfish.framework.Constants;
import com.mrcrayfish.framework.api.network.FrameworkResponse;
import com.mrcrayfish.framework.entity.sync.SyncedEntityData;
import com.mrcrayfish.framework.network.FrameworkCodecs;
import com.mrcrayfish.framework.network.message.ConfigurationMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

/**
 * Author: MrCrayfish
 */
public record S2CSyncedEntityData(Map<ResourceLocation, List<Pair<ResourceLocation, Integer>>> keyMap)
{
    public static final StreamCodec<FriendlyByteBuf, S2CSyncedEntityData> STREAM_CODEC = StreamCodec.composite(
        FrameworkCodecs.ENTITY_DATA_KEYS,
        S2CSyncedEntityData::keyMap,
        S2CSyncedEntityData::new
    );

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
