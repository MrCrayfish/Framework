package com.mrcrayfish.framework.network;

import com.mrcrayfish.framework.api.network.message.ConfigurationMessage;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.network.ConfigurationTask;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class FrameworkTask<T extends ConfigurationMessage<T>> implements ConfigurationTask
{
    private final Type type;
    private final Supplier<List<T>> messages;

    public FrameworkTask(Type type, Supplier<List<T>> messages)
    {
        this.type = type;
        this.messages = messages;
    }

    @Override
    public void start(Consumer<Packet<?>> consumer)
    {

    }

    @Override
    public Type type()
    {
        return this.type;
    }
}
