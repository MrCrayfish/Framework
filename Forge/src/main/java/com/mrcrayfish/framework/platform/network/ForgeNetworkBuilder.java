package com.mrcrayfish.framework.platform.network;

import com.google.common.collect.EnumBiMap;
import com.mrcrayfish.framework.api.network.message.HandshakeMessage;
import com.mrcrayfish.framework.api.network.MessageDirection;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import com.mrcrayfish.framework.api.network.FrameworkNetwork;
import com.mrcrayfish.framework.api.network.FrameworkNetworkBuilder;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.network.message.IMessage;
import com.mrcrayfish.framework.network.message.LoginIndexHolder;
import net.minecraft.Util;
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
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Author: MrCrayfish
 */
public class ForgeNetworkBuilder implements FrameworkNetworkBuilder
{
    private static final EnumBiMap<MessageDirection, NetworkDirection> DIRECTION_MAPPER = Util.make(EnumBiMap.create(MessageDirection.class, NetworkDirection.class), map -> {
        map.put(MessageDirection.PLAY_CLIENT_BOUND, NetworkDirection.PLAY_TO_CLIENT);
        map.put(MessageDirection.PLAY_SERVER_BOUND, NetworkDirection.PLAY_TO_SERVER);
        map.put(MessageDirection.LOGIN_CLIENT_BOUND, NetworkDirection.LOGIN_TO_CLIENT);
        map.put(MessageDirection.LOGIN_SERVER_BOUND, NetworkDirection.LOGIN_TO_SERVER);
    });

    private final ResourceLocation id;
    private final int version;
    private boolean requiresClient = true;
    private boolean requiresServer = true;
    private final AtomicInteger idCount = new AtomicInteger(1);
    private final List<Consumer<SimpleChannel>> playMessages = new ArrayList<>();
    private final List<Consumer<SimpleChannel>> handshakeMessages = new ArrayList<>();

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
            Constructor<T> constructor = messageClass.getDeclaredConstructor();
            T message = constructor.newInstance();
            NetworkDirection networkDirection = DIRECTION_MAPPER.get(direction);
            this.playMessages.add(channel -> channel.registerMessage(this.idCount.getAndIncrement(), messageClass, message::encode, message::decode, (msg, context) -> {
                MessageDirection dir = DIRECTION_MAPPER.inverse().get(context.get().getDirection());
                MessageContext messageContext = new ForgeMessageContext(context.get(), dir);
                message.handle(msg, messageContext);
                IMessage<?> reply = messageContext.getReply();
                if(reply != null) channel.reply(reply, context.get());
            }, Optional.ofNullable(networkDirection)));
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
    public <T extends HandshakeMessage<T>> ForgeNetworkBuilder registerHandshakeMessage(Class<T> messageClass, boolean sendOnLogIn)
    {
        return registerHandshakeMessage(messageClass, sendOnLogIn ? FrameworkNetworkBuilder.createLoginMessageSupplier(messageClass) : null);
    }

    @Override
    public <T extends HandshakeMessage<T>> ForgeNetworkBuilder registerHandshakeMessage(Class<T> messageClass, @Nullable Function<Boolean, List<Pair<String, T>>> messages)
    {
        try
        {
            Constructor<T> constructor = messageClass.getDeclaredConstructor();
            T message = constructor.newInstance();
            this.handshakeMessages.add(channel ->
            {
                SimpleChannel.MessageBuilder<T> builder = channel.messageBuilder(messageClass, this.idCount.getAndIncrement());
                builder.loginIndex(LoginIndexHolder::getLoginIndex, LoginIndexHolder::setLoginIndex);
                builder.encoder(message::encode);
                builder.decoder(message::decode);
                builder.consumerNetworkThread((msg, context) -> {
                    MessageDirection direction = DIRECTION_MAPPER.inverse().get(context.get().getDirection());
                    MessageContext messageContext = new ForgeMessageContext(context.get(), direction);
                    msg.handle(msg, messageContext);
                    IMessage<?> reply = messageContext.getReply();
                    if(context.get().getDirection() == NetworkDirection.LOGIN_TO_CLIENT) {
                        Objects.requireNonNull(reply, "Handshake messages received on the client must reply with an acknowledgement/message");
                        channel.reply(reply, context.get());
                    }
                });
                if(messages != null)
                {
                    builder.buildLoginPacketList(messages);
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
                .consumerNetworkThread((acknowledge1, context) -> {
                    HandshakeHandler.indexFirst((handler, msg, s) -> {
                        MessageDirection direction = DIRECTION_MAPPER.inverse().get(s.get().getDirection());
                        acknowledge.handle(acknowledge1, new ForgeMessageContext(s.get(), direction));
                    }).accept(acknowledge1, context);
                })
                .add();
    }

    public FrameworkNetwork build()
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
        return new ForgeNetwork(channel);
    }
}
