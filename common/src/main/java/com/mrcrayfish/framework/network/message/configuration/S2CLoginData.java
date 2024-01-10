package com.mrcrayfish.framework.network.message.configuration;

import com.mrcrayfish.framework.api.data.login.ILoginData;
import com.mrcrayfish.framework.api.network.FrameworkResponse;
import com.mrcrayfish.framework.network.LoginDataManager;
import com.mrcrayfish.framework.platform.Services;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

/**
 * Author: MrCrayfish
 */
public record S2CLoginData(ResourceLocation id, FriendlyByteBuf data)
{
    public static void encode(S2CLoginData message, FriendlyByteBuf buffer)
    {
        buffer.writeResourceLocation(message.id);
        buffer.writeVarInt(message.data.readableBytes());
        buffer.writeBytes(message.data);
    }

    public static S2CLoginData decode(FriendlyByteBuf buffer)
    {
        ResourceLocation id = buffer.readResourceLocation();
        int readableBytes = buffer.readVarInt();
        FriendlyByteBuf data = new FriendlyByteBuf(Unpooled.wrappedBuffer(buffer.readBytes(readableBytes)));
        return new S2CLoginData(id, data);
    }

    public static FrameworkResponse handle(S2CLoginData message, Consumer<Runnable> executor)
    {
        String[] response = new String[1];
        CountDownLatch block = new CountDownLatch(1);
        executor.accept(() -> {
            ILoginData data = LoginDataManager.getLoginDataSupplier(message.id).get();
            data.readData(message.data).ifPresent(s -> response[0] = s);
            block.countDown();
        });

        try
        {
            block.await();
        }
        catch(InterruptedException e)
        {
            Thread.interrupted();
        }

        if(response[0] != null)
        {
            String modName = Services.PLATFORM.getModName(message.id.getNamespace());
            return FrameworkResponse.error("[%s] %s".formatted(modName, response[0]));

        }

        return FrameworkResponse.SUCCESS;
    }
}
