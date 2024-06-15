package com.mrcrayfish.framework.util;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.core.file.FileWatcher;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.mrcrayfish.framework.Constants;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Author: MrCrayfish
 */
public class ConfigHelper
{
    public static void createBackup(UnmodifiableConfig config)
    {
        if(config instanceof FileConfig fileConfig)
        {
            Path configPath = fileConfig.getNioPath();
            try
            {
                // The length check prevents backing up on initial creation of the config file
                // It also doesn't really make sense to back up an empty file
                if(Files.exists(configPath) && fileConfig.getFile().length() > 0)
                {
                    Path backupPath = configPath.getParent().resolve(fileConfig.getFile().getName() + ".bak");
                    Files.copy(configPath, backupPath, StandardCopyOption.REPLACE_EXISTING);
                }
            }
            catch(IOException e)
            {
                Constants.LOG.debug("Failed to backup config: " + configPath);
                throw new RuntimeException(e);
            }
        }
    }

    public static void unwatchConfig(UnmodifiableConfig config)
    {
        if(config instanceof FileConfig fileConfig)
        {
            Path path = fileConfig.getNioPath();
            try
            {
                FileWatcher.defaultInstance().removeWatch(path);
            }
            catch(RuntimeException e)
            {
                Constants.LOG.debug("Failed to unwatch config: " + path, e);
            }
        }
    }

    public static void loadConfig(UnmodifiableConfig config)
    {
        if(config instanceof FileConfig fileConfig)
        {
            try
            {
                fileConfig.load();
            }
            catch(Exception ignored)
            {
                //TODO error handling
            }
        }
    }

    public static void saveConfig(UnmodifiableConfig config)
    {
        if(config instanceof FileConfig fileConfig)
        {
            fileConfig.save();
        }
    }

    public static void watchConfig(UnmodifiableConfig config, Runnable callback)
    {
        if(config instanceof FileConfig fileConfig)
        {
            Path path = fileConfig.getNioPath();
            try
            {
                FileWatcher.defaultInstance().setWatch(path, callback);
                Constants.LOG.debug("Watching config: " + path);
            }
            catch(Exception e)
            {
                Constants.LOG.debug("Failed to watch config: " + path, e);
                throw new RuntimeException(e);
            }
        }
    }

    public static byte[] readBytes(Path path)
    {
        try
        {
            return Files.readAllBytes(path);
        }
        catch(IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static byte[] getBytes(UnmodifiableConfig config)
    {
        if(config instanceof FileConfig fc)
        {
            return readBytes(fc.getNioPath());
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        TomlFormat.instance().createWriter().write(config, stream);
        return stream.toByteArray();
    }
}
