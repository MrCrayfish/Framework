package com.mrcrayfish.framework.platform.network;

import com.mrcrayfish.framework.api.network.FrameworkNetwork;
import com.mrcrayfish.framework.api.network.FrameworkNetworkBuilder;
import com.mrcrayfish.framework.api.network.MessageDirection;
import com.mrcrayfish.framework.api.network.message.ConfigurationMessage;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class FabricNetworkBuilder implements FrameworkNetworkBuilder
{
    private final ResourceLocation id;
    private final int version;
    private final AtomicInteger idCount = new AtomicInteger(1);
    private final List<FabricMessage<?>> playMessages = new ArrayList<>();
    private final List<BiFunction<FabricNetwork, ServerConfigurationPacketListenerImpl, ConfigurationTask>> configurationTasks = new ArrayList<>();

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
    public <T extends ConfigurationMessage<T>> FrameworkNetworkBuilder registerConfigurationMessage(Class<T> taskClass, String name, Supplier<List<T>> messages)
    {
        try
        {
            ConfigurationTask.Type type = new ConfigurationTask.Type(this.id.withPath(name).toString());
            T message = taskClass.getDeclaredConstructor().newInstance();
            this.playMessages.add(new FabricMessage<>(this.idCount.getAndIncrement(), taskClass, message::encode, message::decode, message::handle, MessageDirection.PLAY_CLIENT_BOUND));
            this.configurationTasks.add((network, handler) -> new FabricConfigurationTask<>(network, handler, type, messages));
        }
        catch(NoSuchMethodException e)
        {
            throw new IllegalArgumentException(String.format("The message %s is missing an empty parameter constructor", taskClass.getName()), e);
        }
        catch(IllegalAccessException e)
        {
            throw new IllegalArgumentException(String.format("Unable to access the constructor of %s. Make sure the constructor is public.", taskClass.getName()), e);
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
        if(!this.configurationTasks.isEmpty())
        {
            this.registerPlayMessage(ConfigurationMessage.Acknowledge.class, MessageDirection.PLAY_SERVER_BOUND);
        }
        return new FabricNetwork(this.id, this.version, this.playMessages, this.configurationTasks);
    }
}
