package com.mrcrayfish.framework.network.message.configuration;

import com.mrcrayfish.framework.Constants;
import com.mrcrayfish.framework.api.network.ConfigurationMessageContext;
import com.mrcrayfish.framework.config.FrameworkConfigManager;
import com.mrcrayfish.framework.network.message.ConfigurationMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.concurrent.CountDownLatch;

/**
 * Author: MrCrayfish
 */
public record S2CConfigData(Identifier key, byte[] data)
{
    public static final StreamCodec<FriendlyByteBuf, S2CConfigData> STREAM_CODEC = StreamCodec.of((buf, data) -> {
        buf.writeIdentifier(data.key);
        buf.writeBytes(data.data);
    }, buf -> {
        Identifier key = buf.readIdentifier();
        byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);
        return new S2CConfigData(key, data);
    });

    public static void handle(S2CConfigData message, ConfigurationMessageContext context)
    {
        Constants.LOG.debug(ConfigurationMessage.MARKER, "Receiving config data from server: '{}'", message.key());
        boolean[] failed = new boolean[1];
        CountDownLatch block = new CountDownLatch(1);
        context.execute(() -> {
            try {
                if(!FrameworkConfigManager.getInstance().processConfigData(message)) {
                    failed[0] = true;
                }
            } catch (Exception e) {
                failed[0] = true;
                Constants.LOG.error(ConfigurationMessage.MARKER, "Fatal error when trying to process sync config", e);
            }
            block.countDown();
        });

        // Wait for processing to finish
        try
        {
            block.await();
        }
        catch(InterruptedException ignored)
        {
            failed[0] = true;
        }

        if(failed[0])
        {
            context.disconnect(Component.translatable("framework.gui.sync_config_failed"));
        }
    }
}
