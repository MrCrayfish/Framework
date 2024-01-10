package com.mrcrayfish.framework.platform.network;

import com.mrcrayfish.framework.Constants;
import com.mrcrayfish.framework.network.message.ConfigurationMessage;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraftforge.network.SimpleChannel;
import net.minecraftforge.network.config.ConfigurationTaskContext;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class ForgeConfigurationTask <T> implements ConfigurationTask
{
    private final SimpleChannel channel;
    private final Type type;
    private final Supplier<List<T>> messages;

    public ForgeConfigurationTask(SimpleChannel channel, Type type, Supplier<List<T>> messages)
    {
        this.channel = channel;
        this.type = type;
        this.messages = messages;
    }

    @Override
    public void start(ConfigurationTaskContext context)
    {
        Constants.LOG.debug(ConfigurationMessage.MARKER, "Sending configuration task '%s'".formatted(this.type.id()));
        this.messages.get().forEach(msg -> this.channel.send(msg, context.getConnection()));
        context.finish(this.type);
    }

    @Override
    public void start(Consumer<Packet<?>> consumer) {}

    @Override
    public Type type()
    {
        return this.type;
    }
}
