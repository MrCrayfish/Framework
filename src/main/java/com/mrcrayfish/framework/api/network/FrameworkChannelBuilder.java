package com.mrcrayfish.framework.api.network;

import com.mrcrayfish.framework.network.message.handshake.LoginIndexHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.HandshakeHandler;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Author: MrCrayfish
 */
public class FrameworkChannelBuilder
{
    private final ResourceLocation id;
    private final int version;
    private boolean requiresClient = true;
    private boolean requiresServer = true;
    private final AtomicInteger idCount = new AtomicInteger(1);
    private final List<Consumer<SimpleChannel>> playMessages = new ArrayList<>();
    private final List<Consumer<SimpleChannel>> handshakeMessages = new ArrayList<>();

    private FrameworkChannelBuilder(ResourceLocation id, int version)
    {
        this.id = id;
        this.version = version;
    }

    public FrameworkChannelBuilder ignoreClient()
    {
        this.requiresClient = false;
        return this;
    }

    public FrameworkChannelBuilder ignoreServer()
    {
        this.requiresServer = false;
        return this;
    }

    public <MSG extends PlayMessage<MSG>> FrameworkChannelBuilder registerPlayMessage(Class<MSG> messageClass)
    {
        return registerPlayMessage(messageClass, null);
    }

    public <MSG extends PlayMessage<MSG>> FrameworkChannelBuilder registerPlayMessage(Class<MSG> messageClass, @Nullable NetworkDirection direction)
    {
        try
        {
            Constructor<MSG> constructor = messageClass.getDeclaredConstructor();
            MSG message = constructor.newInstance();
            this.playMessages.add(channel -> channel.registerMessage(this.idCount.getAndIncrement(), messageClass, message::encode, message::decode, message::handle, Optional.ofNullable(direction)));
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

    public <MSG extends HandshakeMessage<MSG>> FrameworkChannelBuilder registerHandshakeMessage(Class<MSG> messageClass)
    {
        return registerHandshakeMessage(messageClass, null);
    }

    public <MSG extends HandshakeMessage<MSG>> FrameworkChannelBuilder registerHandshakeMessage(Class<MSG> messageClass, @Nullable Function<Boolean, List<Pair<String,MSG>>> messages)
    {
        try
        {
            Constructor<MSG> constructor = messageClass.getDeclaredConstructor();
            MSG message = constructor.newInstance();
            this.handshakeMessages.add(channel ->
            {
                SimpleChannel.MessageBuilder<MSG> builder = channel.messageBuilder(messageClass, this.idCount.getAndIncrement());
                builder.loginIndex(LoginIndexHolder::getLoginIndex, LoginIndexHolder::setLoginIndex);
                builder.encoder(message::encode);
                builder.decoder(message::decode);
                builder.consumer(message::handle);
                if(messages != null)
                {
                    builder.buildLoginPacketList(messages);
                }
                else
                {
                    builder.markAsLoginPacket();
                }
                builder.add();
            });
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

    private void registerAckMessage(SimpleChannel channel)
    {
        HandshakeMessage.Acknowledge acknowledge = new HandshakeMessage.Acknowledge();
        channel.messageBuilder(HandshakeMessage.Acknowledge.class, this.idCount.getAndIncrement())
            .loginIndex(HandshakeMessage::getLoginIndex, HandshakeMessage::setLoginIndex)
            .decoder(acknowledge::decode)
            .encoder(acknowledge::encode)
            .consumer(HandshakeHandler.indexFirst((handler, msg, s) -> acknowledge.handle(msg, s)))
            .add();
    }

    public SimpleChannel build()
    {
        this.idCount.set(1);
        final boolean ignoreClient = !this.requiresClient;
        final boolean ignoreServer = !this.requiresServer;
        String protocolVersion = Integer.toString(this.version);
        SimpleChannel channel = NetworkRegistry.ChannelBuilder
                .named(this.id)
                .networkProtocolVersion(() -> protocolVersion)
                .clientAcceptedVersions(s -> ignoreServer || protocolVersion.equals(s))
                .serverAcceptedVersions(s -> ignoreClient || protocolVersion.equals(s))
                .simpleChannel();
        this.playMessages.forEach(consumer -> consumer.accept(channel));
        if(this.handshakeMessages.size() > 0) this.registerAckMessage(channel);
        this.handshakeMessages.forEach(consumer -> consumer.accept(channel));
        return channel;
    }

    public static FrameworkChannelBuilder create(ResourceLocation id, int version)
    {
        return new FrameworkChannelBuilder(id, version);
    }

    public static FrameworkChannelBuilder create(String modId, String channelName, int version)
    {
        return new FrameworkChannelBuilder(new ResourceLocation(modId, channelName), version);
    }
}
