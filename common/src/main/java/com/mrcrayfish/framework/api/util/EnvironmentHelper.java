package com.mrcrayfish.framework.api.util;

import com.mrcrayfish.framework.api.Environment;
import com.mrcrayfish.framework.api.LogicalEnvironment;
import com.mrcrayfish.framework.platform.Services;
import net.minecraft.util.thread.BlockableEventLoop;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class EnvironmentHelper
{
    private static final MutablePair<Supplier<BlockableEventLoop<?>>, Supplier<BlockableEventLoop<?>>> EXECUTOR = new MutablePair<>(() -> null, () -> null);

    public static void setExecutor(LogicalEnvironment env, BlockableEventLoop<?> eventLoop)
    {
        switch(env)
        {
            case CLIENT -> EXECUTOR.setLeft(() -> eventLoop);
            case SERVER -> EXECUTOR.setRight(() -> eventLoop);
        }
    }

    public static Environment getEnvironment()
    {
        return Services.PLATFORM.getEnvironment();
    }

    public static <T> T callOn(Environment env, Supplier<Callable<T>> task)
    {
        if(Services.PLATFORM.getEnvironment() == env)
        {
            try
            {
                return task.get().call();
            }
            catch(Exception e)
            {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public static void runOn(Environment env, Supplier<Runnable> task)
    {
        if(Services.PLATFORM.getEnvironment() == env)
        {
            task.get().run();
        }
    }

    public static void submitOn(Environment env, Supplier<Runnable> task)
    {
        switch(env)
        {
            case CLIENT -> Optional.ofNullable(EXECUTOR.getLeft().get()).ifPresent(e -> e.submit(task.get()));
            case DEDICATED_SERVER -> Optional.ofNullable(EXECUTOR.getRight().get()).ifPresent(e -> e.submit(task.get()));
        }
    }

    public static void submitOn(LogicalEnvironment env, Supplier<Runnable> task)
    {
        switch(env)
        {
            case CLIENT -> Optional.ofNullable(EXECUTOR.getLeft().get()).ifPresent(e -> e.submit(task.get()));
            case SERVER -> Optional.ofNullable(EXECUTOR.getRight().get()).ifPresent(e -> e.submit(task.get()));
        }
    }
}
