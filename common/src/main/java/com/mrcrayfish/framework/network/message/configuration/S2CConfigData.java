package com.mrcrayfish.framework.network.message.configuration;

import com.mrcrayfish.framework.Constants;
import com.mrcrayfish.framework.api.network.FrameworkResponse;
import com.mrcrayfish.framework.config.FrameworkConfigManager;
import com.mrcrayfish.framework.network.FrameworkCodecs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

/**
 * Author: MrCrayfish
 */
public record S2CConfigData(ResourceLocation key, byte[] data)
{
    public static final StreamCodec<FriendlyByteBuf, S2CConfigData> STREAM_CODEC = StreamCodec.composite(
        ResourceLocation.STREAM_CODEC,
        S2CConfigData::key,
        FrameworkCodecs.BYTE_ARRAY,
        S2CConfigData::data,
        S2CConfigData::new
    );

    public static void encode(S2CConfigData message, FriendlyByteBuf buffer)
    {
        buffer.writeResourceLocation(message.key);
        buffer.writeByteArray(message.data);
    }

    public static S2CConfigData decode(FriendlyByteBuf buffer)
    {
        ResourceLocation key = buffer.readResourceLocation();
        byte[] data = buffer.readByteArray();
        return new S2CConfigData(key, data);
    }

    public static FrameworkResponse handle(S2CConfigData message, Consumer<Runnable> executor)
    {
        Constants.LOG.debug("Received config data from server");
        boolean[] failed = new boolean[1];
        CountDownLatch block = new CountDownLatch(1);
        executor.accept(() -> {
            try {
                if(!FrameworkConfigManager.getInstance().processConfigData(message)) {
                    failed[0] = true;
                }
            } catch (Exception e) {
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
            return FrameworkResponse.error(Component.translatable("configured.gui.handshake_process_failed").getString());
        }
        return FrameworkResponse.SUCCESS;
    }
}
