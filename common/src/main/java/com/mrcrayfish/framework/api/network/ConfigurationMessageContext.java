package com.mrcrayfish.framework.api.network;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Executor;
import java.util.function.Consumer;

/**
 * Author: MrCrayfish
 */
public class ConfigurationMessageContext extends MessageContext
{
    private final Consumer<String> taskCompleter;

    public ConfigurationMessageContext(@Nullable PacketFlow flow, Executor executor, Consumer<Component> disconnect, Consumer<Boolean> handled, Consumer<String> taskCompleter)
    {
        super(flow, executor, disconnect, handled);
        this.taskCompleter = taskCompleter;
    }

    public final void completeTask(String id)
    {
        this.taskCompleter.accept(id);
    }
}
