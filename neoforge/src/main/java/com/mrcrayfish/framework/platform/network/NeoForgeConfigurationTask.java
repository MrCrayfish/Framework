package com.mrcrayfish.framework.platform.network;

import com.mrcrayfish.framework.Constants;
import com.mrcrayfish.framework.network.message.ConfigurationMessage;
import com.mrcrayfish.framework.network.message.configuration.FinishedConfigurationTask;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class NeoForgeConfigurationTask<T> implements ICustomConfigurationTask
{
    private final NeoForgeNetwork network;
    private final Type type;
    private final ServerConfigurationPacketListener listener;
    private final Supplier<List<T>> messages;
    private final boolean completeImmediately;

    public NeoForgeConfigurationTask(NeoForgeNetwork network, ServerConfigurationPacketListener listener, Type type, Supplier<List<T>> messages, boolean completeImmediately)
    {
        this.network = network;
        this.type = type;
        this.listener = listener;
        this.messages = messages;
        this.completeImmediately = completeImmediately;
    }

    @Override
    public void run(Consumer<CustomPacketPayload> sender)
    {
        Constants.LOG.debug(ConfigurationMessage.MARKER, "Sending configuration task '%s'".formatted(this.type.id()));
        this.messages.get().forEach(message -> sender.accept(this.network.encode(message)));
        if(!this.completeImmediately)
        {
            sender.accept(this.network.encode(new FinishedConfigurationTask(this.type, FinishedConfigurationTask.Action.AWAIT)));
        }
        else
        {
            this.listener.finishCurrentTask(this.type);
        }
    }

    @Override
    public Type type()
    {
        return this.type;
    }

    private void finishConfigurationTask()
    {

    }
}
