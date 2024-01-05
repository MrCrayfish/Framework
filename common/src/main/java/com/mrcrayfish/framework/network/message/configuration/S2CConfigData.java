package com.mrcrayfish.framework.network.message.configuration;

import com.mrcrayfish.framework.Constants;
import com.mrcrayfish.framework.api.network.FrameworkResponse;
import com.mrcrayfish.framework.api.network.message.ConfigurationMessage;
import com.mrcrayfish.framework.config.FrameworkConfigManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

/**
 * Author: MrCrayfish
 */
public class S2CConfigData extends ConfigurationMessage<S2CConfigData>
{
    private ResourceLocation key;
    private byte[] data;

    public S2CConfigData() {}

    public S2CConfigData(ResourceLocation key, byte[] data)
    {
        this.key = key;
        this.data = data;
    }

    @Override
    public void encode(S2CConfigData message, FriendlyByteBuf buffer)
    {
        buffer.writeResourceLocation(message.key);
        buffer.writeByteArray(message.data);
    }

    @Override
    public S2CConfigData decode(FriendlyByteBuf buffer)
    {
        ResourceLocation key = buffer.readResourceLocation();
        byte[] data = buffer.readByteArray();
        return new S2CConfigData(key, data);
    }

    @Override
    public FrameworkResponse handle(S2CConfigData message, Consumer<Runnable> executor)
    {
        Constants.LOG.debug("Received config data from server");
        boolean[] failed = new boolean[1];
        CountDownLatch block = new CountDownLatch(1);
        executor.accept(() -> {
            if(!FrameworkConfigManager.getInstance().processConfigData(message)) {
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

    public ResourceLocation getKey()
    {
        return this.key;
    }

    public byte[] getData()
    {
        return this.data;
    }
}
