package com.mrcrayfish.framework.api.network;

import com.mrcrayfish.framework.api.network.message.HandshakeMessage;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * Author: MrCrayfish
 */
public interface FrameworkNetworkBuilder
{
    <T extends PlayMessage<T>> FrameworkNetworkBuilder registerPlayMessage(Class<T> messageClass);

    <T extends PlayMessage<T>> FrameworkNetworkBuilder registerPlayMessage(Class<T> messageClass, @Nullable MessageDirection direction);

    <T extends HandshakeMessage<T>> FrameworkNetworkBuilder registerHandshakeMessage(Class<T> messageClass, boolean sendOnHandshake);

    <T extends HandshakeMessage<T>> FrameworkNetworkBuilder registerHandshakeMessage(Class<T> messageClass, @Nullable Function<Boolean, List<Pair<String, T>>> messages);

    FrameworkNetworkBuilder ignoreClient();

    FrameworkNetworkBuilder ignoreServer();

    FrameworkNetwork build();

    static <T extends HandshakeMessage<T>> Function<Boolean, List<Pair<String, T>>> createHandshakeMessageSupplier(Class<T> messageClass)
    {
        return isLocal ->
        {
            try
            {
                return Collections.singletonList(Pair.of(messageClass.getName(), messageClass.getDeclaredConstructor().newInstance()));
            }
            catch(InstantiationException | IllegalAccessException | NoSuchMethodException |
                  InvocationTargetException e)
            {
                throw new RuntimeException("Inaccessible no-arg constructor for message " + messageClass.getName(), e);
            }
        };
    }
}
