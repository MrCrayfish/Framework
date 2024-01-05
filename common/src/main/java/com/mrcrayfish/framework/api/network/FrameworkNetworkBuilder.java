package com.mrcrayfish.framework.api.network;

import com.mrcrayfish.framework.api.network.message.ConfigurationMessage;
import com.mrcrayfish.framework.api.network.message.PlayMessage;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public interface FrameworkNetworkBuilder
{
    // TODO in future, change messages to use records and use static encode/decode methods to align better with forge/fabric networking

    <T extends PlayMessage<T>> FrameworkNetworkBuilder registerPlayMessage(Class<T> messageClass);

    <T extends PlayMessage<T>> FrameworkNetworkBuilder registerPlayMessage(Class<T> messageClass, @Nullable MessageDirection direction);

    <T extends ConfigurationMessage<T>> FrameworkNetworkBuilder registerConfigurationMessage(Class<T> taskClass, String name, Supplier<List<T>> messages);

    FrameworkNetworkBuilder ignoreClient();

    FrameworkNetworkBuilder ignoreServer();

    FrameworkNetwork build();
}
