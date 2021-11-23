package com.mrcrayfish.framework.api.network;

import com.mrcrayfish.framework.network.message.handshake.C2SAcknowledge;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fmllegacy.network.FMLHandshakeHandler;
import net.minecraftforge.fmllegacy.network.NetworkDirection;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * Author: MrCrayfish
 */
public class ChannelBuilder
{
    private final ResourceLocation id;
    private final int version;
    private boolean requiresClient = true;
    private boolean requiresServer = true;
    private final AtomicInteger idCount = new AtomicInteger(1);
    private final List<Consumer<SimpleChannel>> playMessages = new ArrayList<>();
    private final List<Consumer<SimpleChannel>> handshakeMessages = new ArrayList<>();

    private ChannelBuilder(ResourceLocation id, int version)
    {
        this.id = id;
        this.version = version;
    }

    public ChannelBuilder ignoreClient()
    {
        this.requiresClient = false;
        return this;
    }

    public ChannelBuilder ignoreServer()
    {
        this.requiresServer = false;
        return this;
    }

    public <MSG extends IMessage<MSG>> ChannelBuilder registerPlayMessage(Class<MSG> messageClass)
    {
        return registerPlayMessage(messageClass, null);
    }

    public <MSG extends IMessage<MSG>> ChannelBuilder registerPlayMessage(Class<MSG> messageClass, @Nullable NetworkDirection direction)
    {
        try
        {
            Constructor<MSG> constructor = messageClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            MSG message = constructor.newInstance();
            this.playMessages.add(channel -> channel.registerMessage(this.idCount.getAndIncrement(), messageClass, message::encode, message::decode, message::handle, Optional.ofNullable(direction)));
        }
        catch(NoSuchMethodException e)
        {
            throw new IllegalArgumentException(String.format("The message %s is missing an empty parameter constructor", messageClass.getName()), e);
        }
        catch(InvocationTargetException | InstantiationException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
        return this;
    }

    public <MSG extends LoginIndexedMessage & IMessage<MSG>> ChannelBuilder registerHandshakeMessage(Class<MSG> messageClass)
    {
        try
        {
            Constructor<MSG> constructor = messageClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            MSG message = constructor.newInstance();
            this.handshakeMessages.add(channel -> channel
                .messageBuilder(messageClass, this.idCount.getAndIncrement())
                .loginIndex(LoginIndexedMessage::getLoginIndex, LoginIndexedMessage::setLoginIndex)
                .encoder(message::encode)
                .decoder(message::decode)
                .consumer(FMLHandshakeHandler.biConsumerFor((handler, msg, supplier) -> message.handle(msg, supplier)))
                .markAsLoginPacket()
                .add()
            );
        }
        catch(NoSuchMethodException e)
        {
            throw new IllegalArgumentException(String.format("The message %s is missing an empty parameter constructor", messageClass.getName()), e);
        }
        catch(InvocationTargetException | InstantiationException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
        return this;
    }

    private void registerAckMessage(SimpleChannel channel)
    {
        channel.messageBuilder(C2SAcknowledge.class, this.idCount.getAndIncrement())
            .loginIndex(LoginIndexedMessage::getLoginIndex, LoginIndexedMessage::setLoginIndex)
            .decoder(C2SAcknowledge::decode)
            .encoder(C2SAcknowledge::encode)
            .consumer(FMLHandshakeHandler.indexFirst((handler, msg, s) -> C2SAcknowledge.handle(msg, s)))
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

    public static ChannelBuilder create(ResourceLocation id, int version)
    {
        return new ChannelBuilder(id, version);
    }

    public static ChannelBuilder create(String modId, String channelName, int version)
    {
        return new ChannelBuilder(new ResourceLocation(modId, channelName), version);
    }
}
