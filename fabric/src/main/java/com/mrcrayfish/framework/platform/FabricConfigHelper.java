package com.mrcrayfish.framework.platform;

import com.mrcrayfish.framework.api.config.FrameworkConfig;
import com.mrcrayfish.framework.platform.services.IConfigHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.CustomValue;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class FabricConfigHelper implements IConfigHelper
{
    @Override
    public List<Pair<FrameworkConfig, java.lang.Object>> getAllFrameworkConfigs()
    {
        List<Pair<FrameworkConfig, Object>> configs = new ArrayList<>();
        FabricLoader.getInstance().getAllMods().forEach(container ->
        {
            CustomValue value = container.getMetadata().getCustomValue("framework");
            if(value == null || value.getType() != CustomValue.CvType.OBJECT)
                return;

            CustomValue.CvObject configuredObj = value.getAsObject();
            CustomValue configsValue = configuredObj.get("configs");
            if(configsValue == null || configsValue.getType() != CustomValue.CvType.ARRAY)
                return;

            CustomValue.CvArray configsArray = configsValue.getAsArray();
            configsArray.forEach(elementValue ->
            {
                if(elementValue.getType() != CustomValue.CvType.STRING)
                    return;

                try
                {
                    String className = elementValue.getAsString();
                    Class<?> configClass = Class.forName(className);
                    for(Field field : configClass.getDeclaredFields())
                    {
                        FrameworkConfig config = field.getDeclaredAnnotation(FrameworkConfig.class);
                        if(config == null)
                            continue;

                        field.setAccessible(true);
                        if(!Modifier.isStatic(field.getModifiers()))
                            throw new RuntimeException("Fields annotated with @FrameworkConfig must be static");

                        Object object = field.get(null);
                        configs.add(Pair.of(config, object));
                    }
                }
                catch(ClassNotFoundException | IllegalAccessException e)
                {
                    throw new RuntimeException(e);
                }
            });
        });
        return configs;
    }

    @Override
    public Path getGamePath()
    {
        return FabricLoader.getInstance().getGameDir();
    }

    @Override
    public Path getConfigPath()
    {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public String getDefaultConfigPath()
    {
        return "defaultconfigs";
    }
}
