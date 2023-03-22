package com.mrcrayfish.framework.api.util;

import com.mrcrayfish.framework.FrameworkData;
import com.mrcrayfish.framework.api.Environment;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class EnvironmentHelper
{
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
}
