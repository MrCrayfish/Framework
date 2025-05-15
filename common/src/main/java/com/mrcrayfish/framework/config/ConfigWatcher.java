package com.mrcrayfish.framework.config;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.core.file.FileWatcher;
import com.mrcrayfish.framework.Constants;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Author: MrCrayfish
 */
public class ConfigWatcher
{
    private static volatile ConfigWatcher instance;

    public static synchronized ConfigWatcher get()
    {
        if(instance == null)
        {
            instance = new ConfigWatcher();
        }
        return instance;
    }

    private final FileWatcher fileWatcher = new FileWatcher();

    public boolean watch(UnmodifiableConfig config, Runnable changeCallback)
    {
        if(config instanceof FileConfig fileConfig)
        {
            Path path = fileConfig.getNioPath();
            try
            {
                this.fileWatcher.setWatch(path, changeCallback);
                Constants.LOG.debug("Started watching config: " + path);
                return true;
            }
            catch(Exception e)
            {
                Constants.LOG.debug("Failed to watch config: " + path, e);
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    public void unwatch(UnmodifiableConfig config)
    {
        if(config instanceof FileConfig fileConfig)
        {
            Path path = fileConfig.getNioPath();
            try
            {
                this.fileWatcher.removeWatch(path);
                Constants.LOG.debug("Stopped watching config: " + path);
            }
            catch(RuntimeException e)
            {
                Constants.LOG.debug("Failed to unwatch config: " + path, e);
            }
        }
    }

    public void stop()
    {
        try
        {
            Constants.LOG.debug("Stopping config watcher");
            this.fileWatcher.stop();
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
