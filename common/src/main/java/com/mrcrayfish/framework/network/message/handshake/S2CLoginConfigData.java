package com.mrcrayfish.framework.network.message.handshake;

import com.mrcrayfish.framework.Constants;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.HandshakeMessage;
import com.mrcrayfish.framework.config.FrameworkConfigManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.CountDownLatch;

/**
 * Author: MrCrayfish
 */
public class S2CLoginConfigData extends HandshakeMessage<S2CLoginConfigData>
{
    private ResourceLocation key;
    private byte[] data;

    public S2CLoginConfigData() {}

    public S2CLoginConfigData(ResourceLocation key, byte[] data)
    {
        this.key = key;
        this.data = data;
    }

    @Override
    public void encode(S2CLoginConfigData message, FriendlyByteBuf buffer)
    {
        buffer.writeResourceLocation(message.key);
        buffer.writeByteArray(message.data);
    }

    @Override
    public S2CLoginConfigData decode(FriendlyByteBuf buffer)
    {
        ResourceLocation key = buffer.readResourceLocation();
        byte[] data = buffer.readByteArray();
        return new S2CLoginConfigData(key, data);
    }

    @Override
    public void handle(S2CLoginConfigData message, MessageContext context)
    {
        Constants.LOG.debug("Received config data from server");
        boolean[] failed = new boolean[1];
        CountDownLatch block = new CountDownLatch(1);
        context.execute(() -> {
            try {
                if(!FrameworkConfigManager.getInstance().processConfigData(message)) {
                    failed[0] = true;
                }
            } catch (Exception e) {
                failed[0] = true;
                Constants.LOG.error("Failed to process config data from server: %s".formatted(message.key), e);
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
            context.getNetworkManager().disconnect(Component.translatable("framework.gui.process_config_fail", message.key.toString()));
        }
        context.setHandled(true);
        context.reply(new Acknowledge());
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
