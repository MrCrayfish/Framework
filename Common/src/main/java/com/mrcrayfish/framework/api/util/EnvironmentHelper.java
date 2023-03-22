package com.mrcrayfish.framework.api.util;

import com.mrcrayfish.framework.FrameworkData;
import com.mrcrayfish.framework.api.Environment;
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

    public static void setExecutor(Environment env, BlockableEventLoop<?> eventLoop)
    {
        switch(env)
        {
            case CLIENT -> EXECUTOR.setLeft(() -> eventLoop);
            case DEDICATED_SERVER -> EXECUTOR.setRight(() -> eventLoop);
        }
    }

    public static Environment getEnvironment()
    {
        return FrameworkData.getEnvironment();
    }

    public static <T> T callOn(Environment env, Supplier<Callable<T>> task)
    {
        if(FrameworkData.getEnvironment() == env)
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
        if(FrameworkData.getEnvironment() == env)
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
}
