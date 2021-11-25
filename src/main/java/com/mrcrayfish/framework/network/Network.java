package com.mrcrayfish.framework.network;

import com.mrcrayfish.framework.Reference;
import com.mrcrayfish.framework.api.data.login.ILoginData;
import com.mrcrayfish.framework.api.network.FrameworkChannelBuilder;
import com.mrcrayfish.framework.network.message.handshake.S2CLoginData;
import com.mrcrayfish.framework.network.message.handshake.S2CSyncedPlayerData;
import com.mrcrayfish.framework.network.message.play.S2CUpdatePlayerData;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fmllegacy.network.NetworkDirection;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;
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
    private static final SimpleChannel HANDSHAKE_CHANNEL = FrameworkChannelBuilder
            .create(Reference.MOD_ID, "handshake", 1)
            .registerHandshakeMessage(S2CSyncedPlayerData.class)
            .registerHandshakeMessage(S2CLoginData.class, Network::getLoginDataMessages)
            .build();

    private static final SimpleChannel PLAY_CHANNEL = FrameworkChannelBuilder
            .create(Reference.MOD_ID, "play", 1)
            .registerPlayMessage(S2CUpdatePlayerData.class, NetworkDirection.PLAY_TO_CLIENT)
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

    public static void registerLoginData(ResourceLocation id, Supplier<? extends ILoginData> data)
    {
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
