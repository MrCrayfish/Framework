package com.mrcrayfish.framework.network.message.handshake;

import com.mrcrayfish.framework.api.data.login.ILoginData;
import com.mrcrayfish.framework.api.network.HandshakeMessage;
import com.mrcrayfish.framework.network.Network;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.network.NetworkEvent;

import java.util.concurrent.CountDownLatch;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class S2CLoginData extends HandshakeMessage<S2CLoginData>
{
    private ResourceLocation id;
    private FriendlyByteBuf data;

    public S2CLoginData() {}

    public S2CLoginData(ResourceLocation id, FriendlyByteBuf data)
    {
        this.id = id;
        this.data = data;
    }

    @Override
    public void encode(S2CLoginData message, FriendlyByteBuf buffer)
    {
        buffer.writeResourceLocation(message.id);
        buffer.writeVarInt(message.data.readableBytes());
        buffer.writeBytes(message.data);
    }

    @Override
    public S2CLoginData decode(FriendlyByteBuf buffer)
    {
        ResourceLocation id = buffer.readResourceLocation();
        int readableBytes = buffer.readVarInt();
        FriendlyByteBuf data = new FriendlyByteBuf(Unpooled.wrappedBuffer(buffer.readBytes(readableBytes)));
        return new S2CLoginData(id, data);
    }

    @Override
    public void handle(S2CLoginData message, Supplier<NetworkEvent.Context> supplier)
    {
        String[] response = new String[1];
        CountDownLatch block = new CountDownLatch(1);
        supplier.get().enqueueWork(() ->
        {
            ILoginData data = Network.getLoginDataSupplier(message.id).get();
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
            String modName = ModList.get().getModContainerById(message.id.getNamespace()).map(container -> container.getModInfo().getDisplayName()).orElse("Framework");
            supplier.get().getNetworkManager().disconnect(Component.literal("Connection closed - [" + modName + "] " + response[0]));
            return;
        }

        supplier.get().setPacketHandled(true);
        Network.getHandshakeChannel().reply(new Acknowledge(), supplier.get());
    }
}
