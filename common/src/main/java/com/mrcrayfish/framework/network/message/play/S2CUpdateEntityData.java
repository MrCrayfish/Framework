package com.mrcrayfish.framework.network.message.play;

import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.client.multiplayer.ClientPlayHandler;
import com.mrcrayfish.framework.entity.sync.DataEntry;
import com.mrcrayfish.framework.network.FrameworkCodecs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public record S2CUpdateEntityData(int entityId, List<DataEntry<?, ?>> entries)
{
    public static final StreamCodec<RegistryFriendlyByteBuf, S2CUpdateEntityData> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT,
        S2CUpdateEntityData::entityId,
        FrameworkCodecs.DATA_ENTRIES,
        S2CUpdateEntityData::entries,
        S2CUpdateEntityData::new
    );

    public static void handle(S2CUpdateEntityData message, MessageContext context)
    {
        context.execute(() -> ClientPlayHandler.handleSyncEntityData(message));
        context.setHandled(true);
    }
}
