package com.mrcrayfish.framework.api.event;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Author: MrCrayfish
 */
public final class FrameworkEvent<T extends IFrameworkEvent>
{
    private final List<T> listeners = new ArrayList<>();
    private final Function<List<T>, T> invoker;

    public FrameworkEvent(Function<List<T>, T> invoker)
    {
        this.invoker = invoker;
    }

    public void register(T listener)
    {
        this.listeners.add(listener);
    }

    public T post()
    {
        return this.invoker.apply(this.listeners);
    }
}
