package com.mrcrayfish.framework.network;

import com.mrcrayfish.framework.api.data.login.ILoginData;
import com.mrcrayfish.framework.network.message.configuration.S2CLoginData;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Author: MrCrayfish
 */
public class LoginDataManager
{
    private static final Map<ResourceLocation, Supplier<? extends ILoginData>> LOGIN_DATA = new ConcurrentHashMap<>();

    public static List<S2CLoginData> getConfigurationMessages()
    {
        return LOGIN_DATA.entrySet().stream().map(entry -> {
            ResourceLocation id = entry.getKey();
            ILoginData data = entry.getValue().get();
            FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
            data.writeData(buffer);
            return new S2CLoginData(id, buffer);
        }).collect(Collectors.toList());
    }

    public synchronized static void registerLoginData(ResourceLocation id, Supplier<? extends ILoginData> data)
    {
        /*if(Framework.isGameLoaded())
        {
            throw new IllegalStateException(String.format("Tried to register login data '%s' after game initialization", id.toString()));
        }*/
        LOGIN_DATA.putIfAbsent(id, data);
    }

    public static Supplier<? extends ILoginData> getLoginDataSupplier(ResourceLocation id)
    {
        return LOGIN_DATA.get(id);
    }

    public static Map<ResourceLocation, Supplier<? extends ILoginData>> getLoginData()
    {
        return LOGIN_DATA;
    }
}
