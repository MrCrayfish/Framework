package com.mrcrayfish.framework.api.util;

import com.mrcrayfish.framework.api.Environment;
import com.mrcrayfish.framework.api.LogicalEnvironment;
import com.mrcrayfish.framework.platform.Services;
import net.minecraft.util.thread.BlockableEventLoop;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class TaskRunner
{
    private static final MutablePair<BlockableEventLoop<?>, BlockableEventLoop<?>> EXECUTOR = new MutablePair<>(null, null);

    public static void setExecutor(LogicalEnvironment env, BlockableEventLoop<?> eventLoop)
    {
        switch(env)
        {
            case CLIENT -> EXECUTOR.setLeft(eventLoop);
            case SERVER -> EXECUTOR.setRight(eventLoop);
        }
    }

    public static <T> T callIf(Environment env, Supplier<Supplier<T>> task)
    {
        if(Services.PLATFORM.getEnvironment() == env)
        {
            return task.get().get();
        }
        return null;
    }

    public static void runIf(Environment env, Supplier<Runnable> task)
    {
        if(Services.PLATFORM.getEnvironment() == env)
        {
            task.get().run();
        }
    }

    public static void submitOn(Environment env, Supplier<Runnable> task)
    {
        submitOn(env.getLogical(), task);
    }

    public static void submitOn(LogicalEnvironment env, Supplier<Runnable> task)
    {
        switch(env)
        {
            case CLIENT -> Optional.ofNullable(EXECUTOR.getLeft()).ifPresent(e -> e.submit(task.get()));
            case SERVER -> Optional.ofNullable(EXECUTOR.getRight()).ifPresent(e -> e.submit(task.get()));
        }
    }
}
