package com.mrcrayfish.framework.network.message.configuration;

import com.mrcrayfish.framework.api.network.ConfigurationMessageContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.network.ConfigurationTask;

/**
 * Author: MrCrayfish
 */
public record FinishedConfigurationTask(String id, Action action)
{
    public FinishedConfigurationTask(ConfigurationTask.Type type, Action action)
    {
        this(type.id(), action);
    }

    public static final StreamCodec<FriendlyByteBuf, FinishedConfigurationTask> STREAM_CODEC = StreamCodec.of((buf, task) -> {
        buf.writeUtf(task.id);
        buf.writeEnum(task.action);
    }, buf -> {
        String id = buf.readUtf();
        Action action = buf.readEnum(Action.class);
        return new FinishedConfigurationTask(id, action);
    });

    public static void handle(FinishedConfigurationTask message, ConfigurationMessageContext context)
    {
        switch(message.action()) {
            case AWAIT -> context.reply(new FinishedConfigurationTask(message.id(), Action.COMPLETED));
            case COMPLETED -> context.completeTask(message.id());
        }
        context.setHandled(true);
    }

    public enum Action
    {
        AWAIT, COMPLETED;
    }
}
