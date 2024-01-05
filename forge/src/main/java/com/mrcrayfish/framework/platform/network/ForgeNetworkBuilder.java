package com.mrcrayfish.framework.platform.network;

import com.google.common.collect.EnumBiMap;
import com.mrcrayfish.framework.api.network.FrameworkNetwork;
import com.mrcrayfish.framework.api.network.FrameworkNetworkBuilder;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.MessageDirection;
import com.mrcrayfish.framework.api.network.message.ConfigurationMessage;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import com.mrcrayfish.framework.network.message.IMessage;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.SimpleChannel;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class ForgeNetworkBuilder implements FrameworkNetworkBuilder
{
    private static final EnumBiMap<MessageDirection, NetworkDirection> DIRECTION_MAPPER = Util.make(EnumBiMap.create(MessageDirection.class, NetworkDirection.class), map -> {
        map.put(MessageDirection.PLAY_CLIENT_BOUND, NetworkDirection.PLAY_TO_CLIENT);
        map.put(MessageDirection.PLAY_SERVER_BOUND, NetworkDirection.PLAY_TO_SERVER);
        map.put(MessageDirection.HANDSHAKE_CLIENT_BOUND, NetworkDirection.LOGIN_TO_CLIENT);
        map.put(MessageDirection.HANDSHAKE_SERVER_BOUND, NetworkDirection.LOGIN_TO_SERVER);
    });

    private final ResourceLocation id;
    private final int version;
    private boolean requiresClient = true;
    private boolean requiresServer = true;
    private final List<Consumer<SimpleChannel>> playMessages = new ArrayList<>();
    private final List<Consumer<SimpleChannel>> configurationMessages = new ArrayList<>();
    private final List<Function<SimpleChannel, ConfigurationTask>> configurationTasks = new ArrayList<>();

    public ForgeNetworkBuilder(ResourceLocation id, int version)
    {
        this.id = id;
        this.version = version;
    }

    @Override
    public ForgeNetworkBuilder ignoreClient()
    {
        this.requiresClient = false;
        return this;
    }

    public ForgeNetworkBuilder ignoreServer()
    {
        this.requiresServer = false;
        return this;
    }

    @Override
    public <T extends PlayMessage<T>> ForgeNetworkBuilder registerPlayMessage(Class<T> messageClass)
    {
        return registerPlayMessage(messageClass, null);
    }

    @Override
    public <T extends PlayMessage<T>> ForgeNetworkBuilder registerPlayMessage(Class<T> messageClass, @Nullable MessageDirection direction)
    {
        try
        {
            T message = messageClass.getDeclaredConstructor().newInstance();
            NetworkDirection networkDirection = DIRECTION_MAPPER.get(direction);
            this.playMessages.add(channel -> channel.messageBuilder(messageClass, networkDirection)
                .encoder(message::encode)
                .decoder(message::decode)
                .consumerNetworkThread((msg, context) -> {
                    MessageDirection dir = DIRECTION_MAPPER.inverse().get(context.getDirection());
                    MessageContext messageContext = new ForgeMessageContext(context, dir);
                    message.handle(msg, messageContext);
                    IMessage<?> reply = messageContext.getReply();
                    if(reply != null) {
                        channel.reply(reply, context);
                    }
                }).add());
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
    public <T extends ConfigurationMessage<T>> FrameworkNetworkBuilder registerConfigurationMessage(Class<T> configurationClass, String name, Supplier<List<T>> messages)
    {
        try
        {
            ConfigurationTask.Type type = new ConfigurationTask.Type(this.id.withPath(name).toString());
            this.configurationTasks.add(channel -> new ForgeConfigurationTask<>(channel, type, messages));
            T message = configurationClass.getDeclaredConstructor().newInstance();
            NetworkDirection networkDirection = DIRECTION_MAPPER.get(MessageDirection.PLAY_CLIENT_BOUND);
            this.configurationMessages.add(channel -> channel.messageBuilder(configurationClass, networkDirection)
                .encoder(message::encode)
                .decoder(message::decode)
                .consumerNetworkThread((msg, context) -> {
                    MessageDirection dir = DIRECTION_MAPPER.inverse().get(context.getDirection());
                    MessageContext messageContext = new ForgeMessageContext(context, dir);
                    message.handle(msg, messageContext);
                    IMessage<?> reply = messageContext.getReply();
                    if(reply != null) {
                        channel.reply(reply, context);
                    }
                }).add());
        }
        catch(NoSuchMethodException e)
        {
            throw new IllegalArgumentException(String.format("The message %s is missing an empty parameter constructor", configurationClass.getName()), e);
        }
        catch(IllegalAccessException e)
        {
            throw new IllegalArgumentException(String.format("Unable to access the constructor of %s. Make sure the constructor is public.", configurationClass.getName()), e);
        }
        catch(InvocationTargetException | InstantiationException e)
        {
            e.printStackTrace();
        }
        return this;
    }

    private void registerConfigurationAckMessage(SimpleChannel channel)
    {
        ConfigurationMessage.Acknowledge acknowledge = new ConfigurationMessage.Acknowledge();
        channel.messageBuilder(ConfigurationMessage.Acknowledge.class, NetworkDirection.PLAY_TO_SERVER)
            .decoder(acknowledge::decode)
            .encoder(acknowledge::encode)
            .consumerNetworkThread((acknowledge1, context) -> {
                MessageDirection direction = DIRECTION_MAPPER.inverse().get(context.getDirection());
                acknowledge.handle(acknowledge1, new ForgeMessageContext(context, direction));
            }).add();
    }

    public FrameworkNetwork build()
    {
        ChannelBuilder builder = ChannelBuilder.named(this.id).networkProtocolVersion(this.version);
        if(!this.requiresClient) builder.optionalClient();
        if(!this.requiresServer) builder.optionalServer();
        SimpleChannel channel = builder.simpleChannel();
        this.playMessages.forEach(c -> c.accept(channel));
        if(!this.configurationMessages.isEmpty())
        {
            this.registerConfigurationAckMessage(channel);
            this.configurationMessages.forEach(c -> c.accept(channel));
        }
        return new ForgeNetwork(channel, this.configurationTasks);
    }
}
