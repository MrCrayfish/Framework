package com.mrcrayfish.framework.platform.network;

import com.mrcrayfish.framework.api.network.message.HandshakeMessage;
import com.mrcrayfish.framework.api.network.MessageDirection;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import com.mrcrayfish.framework.api.network.FrameworkNetwork;
import com.mrcrayfish.framework.api.network.FrameworkNetworkBuilder;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * Author: MrCrayfish
 */
public class FabricNetworkBuilder implements FrameworkNetworkBuilder
{
    private final ResourceLocation id;
    private final int version;
    private final AtomicInteger idCount = new AtomicInteger(1);
    private final List<FabricMessage<?>> playMessages = new ArrayList<>();
    private final List<FabricLoginMessage<?>> handshakeMessages = new ArrayList<>();

    public FabricNetworkBuilder(ResourceLocation id, int version)
    {
        this.id = id;
        this.version = version;
    }

    @Override
    public <T extends PlayMessage<T>> FrameworkNetworkBuilder registerPlayMessage(Class<T> messageClass)
    {
        return this.registerPlayMessage(messageClass, null);
    }

    @Override
    public <T extends PlayMessage<T>> FrameworkNetworkBuilder registerPlayMessage(Class<T> messageClass, @Nullable MessageDirection direction)
    {
        try
        {
            Constructor<T> constructor = messageClass.getDeclaredConstructor();
            T message = constructor.newInstance();
            this.playMessages.add(new FabricMessage<>(this.idCount.getAndIncrement(), messageClass, message::encode, message::decode, message::handle, null));
        }
        catch(NoSuchMethodException e)
        {
            throw new IllegalArgumentException(String.format("The message %s is missing an empty parameter constructor", messageClass.getName()), e);
        }
        catch(IllegalAccessException e)
        {
            throw new IllegalArgumentException(String.format("Unable to access the constructor of %s. Make sure the constructor is public.", messageClass.getName()), e);
        }
        catch(InvocationTargetException | InstantiationException e)
        {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public <T extends HandshakeMessage<T>> FrameworkNetworkBuilder registerHandshakeMessage(Class<T> messageClass, boolean sendOnLogIn)
    {
        return this.registerHandshakeMessage(messageClass, sendOnLogIn ? FrameworkNetworkBuilder.createLoginMessageSupplier(messageClass) : null);
    }

    @Override
    public <T extends HandshakeMessage<T>> FrameworkNetworkBuilder registerHandshakeMessage(Class<T> messageClass, @Nullable Function<Boolean, List<Pair<String, T>>> messages)
    {
        try
        {
            Constructor<T> constructor = messageClass.getDeclaredConstructor();
            T message = constructor.newInstance();
            this.handshakeMessages.add(new FabricLoginMessage<>(this.idCount.getAndIncrement(), messageClass, message::encode, message::decode, message::handle, null, messages));
        }
        catch(NoSuchMethodException e)
        {
            throw new IllegalArgumentException(String.format("The message %s is missing an empty parameter constructor", messageClass.getName()), e);
        }
        catch(IllegalAccessException e)
        {
            throw new IllegalArgumentException(String.format("Unable to access the constructor of %s. Make sure the constructor is public.", messageClass.getName()), e);
        }
        catch(InvocationTargetException | InstantiationException e)
        {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public FrameworkNetworkBuilder ignoreClient()
    {
        return this;
    }

    @Override
    public FrameworkNetworkBuilder ignoreServer()
    {
        return this;
    }

    @Override
    public FrameworkNetwork build()
    {
        // Auto register default acknowledge message if network has handshake messages
        if(this.handshakeMessages.size() > 0) this.registerHandshakeMessage(HandshakeMessage.Acknowledge.class, false);
        return new FabricNetwork(this.id, this.version, this.playMessages, this.handshakeMessages);
    }
}
