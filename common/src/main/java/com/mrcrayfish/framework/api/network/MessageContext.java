package com.mrcrayfish.framework.api.network;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketFlow;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

/**
 * Author: MrCrayfish
 */
public abstract class MessageContext
{
    private final @Nullable PacketFlow flow;
    private final Executor executor;
    private final Consumer<Component> disconnect;
    private final Consumer<Boolean> handled;
    private Object reply;

    public MessageContext(@Nullable PacketFlow flow, Executor executor, Consumer<Component> disconnect, Consumer<Boolean> handled)
    {
        this.flow = flow;
        this.executor = executor;
        this.disconnect = disconnect;
        this.handled = handled;
    }

    @Nullable
    public final PacketFlow getFlow()
    {
        return this.flow;
    }

    public final void execute(Runnable runnable)
    {
        this.executor.execute(runnable);
    }

    public final void disconnect(Component reason)
    {
        this.disconnect.accept(Component.literal("Connection closed - ").append(reason));
    }

    public final void reply(Object reply)
    {
        this.reply = reply;
    }

    public final Optional<Object> getReply()
    {
        return Optional.ofNullable(this.reply);
    }

    // Forge/NeoForge
    public final void setHandled(boolean handled)
    {
        this.handled.accept(handled);
    }
}
