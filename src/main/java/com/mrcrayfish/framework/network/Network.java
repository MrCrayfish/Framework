package com.mrcrayfish.framework.network;

import com.mrcrayfish.framework.Framework;
import com.mrcrayfish.framework.Reference;
import com.mrcrayfish.framework.api.data.login.ILoginData;
import com.mrcrayfish.framework.api.network.FrameworkChannelBuilder;
import com.mrcrayfish.framework.network.message.handshake.S2CLoginData;
import com.mrcrayfish.framework.network.message.handshake.S2CSyncedEntityData;
import com.mrcrayfish.framework.network.message.play.S2CUpdateEntityData;
import io.netty.buffer.Unpooled;
import io.netty.util.AttributeKey;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.HandshakeHandler;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Author: MrCrayfish
 */
public class Network
{
    static final AttributeKey<HandshakeHandler> FML_HANDSHAKE_HANDLER = AttributeKey.valueOf("fml:handshake");

    private static final SimpleChannel HANDSHAKE_CHANNEL = FrameworkChannelBuilder
            .create(Reference.MOD_ID, "handshake", 1)
            .registerHandshakeMessage(S2CSyncedEntityData.class)
            .registerHandshakeMessage(S2CLoginData.class, Network::getLoginDataMessages)
            .build();

    private static final SimpleChannel PLAY_CHANNEL = FrameworkChannelBuilder
            .create(Reference.MOD_ID, "play", 1)
            .registerPlayMessage(S2CUpdateEntityData.class, NetworkDirection.PLAY_TO_CLIENT)
            .build();

    private static final Map<ResourceLocation, Supplier<? extends ILoginData>> ID_TO_LOGIN_DATA = new ConcurrentHashMap<>();

    public static void init() {}

    public static SimpleChannel getHandshakeChannel()
    {
        return HANDSHAKE_CHANNEL;
    }

    public static SimpleChannel getPlayChannel()
    {
         return PLAY_CHANNEL;
    }

    public synchronized static void registerLoginData(ResourceLocation id, Supplier<? extends ILoginData> data)
    {
        if(Framework.isGameLoaded())
        {
            throw new IllegalStateException(String.format("Tried to register login data '%s' after game initialization", id.toString()));
        }
        ID_TO_LOGIN_DATA.putIfAbsent(id, data);
    }

    public static Supplier<? extends ILoginData> getLoginDataSupplier(ResourceLocation id)
    {
        return ID_TO_LOGIN_DATA.get(id);
    }

    private static List<Pair<String, S2CLoginData>> getLoginDataMessages(boolean isLocal)
    {
        return ID_TO_LOGIN_DATA.entrySet().stream().map(entry -> {
            ResourceLocation id = entry.getKey();
            ILoginData data = entry.getValue().get();
            FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
            data.writeData(buffer);
            return Pair.of(id.toString(), new S2CLoginData(id, buffer));
        }).collect(Collectors.toList());
    }
}
