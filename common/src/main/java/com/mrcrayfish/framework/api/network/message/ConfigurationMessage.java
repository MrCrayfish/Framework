package com.mrcrayfish.framework.api.network.message;

import com.mrcrayfish.framework.Constants;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.FrameworkResponse;
import com.mrcrayfish.framework.network.message.IMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.function.Consumer;

/**
 * Author: MrCrayfish
 */
public abstract class ConfigurationMessage<T> implements IMessage<T>
{
    public static final Marker CONFIGURATION_MARKER = MarkerFactory.getMarker("FRAMEWORK_CONFIGURATION");

    @Override
    public final void handle(T message, MessageContext context)
    {
        FrameworkResponse response = this.handle(message, context::execute);
        if(response.isError())
        {
            context.getNetworkManager().disconnect(Component.literal("Connection closed - " + response.message()));
            return;
        }
        context.setHandled(true);
        context.reply(new Acknowledge());
    }

    public abstract FrameworkResponse handle(T message, Consumer<Runnable> executor);

    public static class Acknowledge extends PlayMessage<Acknowledge>
    {
        @Override
        public void encode(Acknowledge message, FriendlyByteBuf buffer) {}

        @Override
        public Acknowledge decode(FriendlyByteBuf buf)
        {
            return new Acknowledge();
        }

        @Override
        public void handle(Acknowledge message, MessageContext context)
        {
            Constants.LOG.debug(CONFIGURATION_MARKER, "Received acknowledgement from client");
            context.setHandled(true);
        }
    }
}
