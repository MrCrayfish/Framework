package com.mrcrayfish.framework.platform.network;

import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.MessageDirection;
import net.minecraft.network.FriendlyByteBuf;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Author: MrCrayfish
 */
public class FabricHandshakeMessage<T> extends FabricMessage<T>
{
    private final Function<Boolean, List<T>> messages;

    public FabricHandshakeMessage(int index, Class<T> messageClass, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, MessageContext> handler, @Nullable MessageDirection direction, @Nullable Function<Boolean, List<T>> messages)
    {
        super(index, messageClass, encoder, decoder, handler, direction);
        this.messages = messages;
    }

    @Nullable
    public Function<Boolean, List<T>> getMessages()
    {
        return this.messages;
    }
}
