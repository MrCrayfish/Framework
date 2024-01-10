package com.mrcrayfish.framework.platform;

import com.mrcrayfish.framework.api.config.FrameworkConfig;
import com.mrcrayfish.framework.platform.services.IConfigHelper;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLConfig;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforgespi.language.ModFileScanData;
import org.apache.commons.lang3.tuple.Pair;
import org.objectweb.asm.Type;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public class NeoForgeConfigHelper implements IConfigHelper
{
    private static final Type FRAMEWORK_CONFIG_TYPE = Type.getType(FrameworkConfig.class);

    @Override
    public List<Pair<FrameworkConfig, Object>> getAllFrameworkConfigs()
    {
        List<ModFileScanData.AnnotationData> annotations = ModList.get().getAllScanData().stream().map(ModFileScanData::getAnnotations).flatMap(Collection::stream).filter(a -> FRAMEWORK_CONFIG_TYPE.equals(a.annotationType())).toList();
        List<Pair<FrameworkConfig, Object>> configs = new ArrayList<>();
        annotations.forEach(data ->
        {
            try
            {
                Class<?> configClass = Class.forName(data.clazz().getClassName());
                Field field = configClass.getDeclaredField(data.memberName());
                field.setAccessible(true);
                Object object = field.get(null);
                Optional.ofNullable(field.getDeclaredAnnotation(FrameworkConfig.class)).ifPresent(simpleConfig -> {
                    configs.add(Pair.of(simpleConfig, object));
                });
            }
            catch(NoSuchFieldException | ClassNotFoundException | IllegalAccessException e)
            {
                throw new RuntimeException(e);
            }
        });
        return configs;
    }

    @Override
    public Path getGamePath()
    {
        return FMLPaths.GAMEDIR.get();
    }

    @Override
    public Path getConfigPath()
    {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public String getDefaultConfigPath()
    {
        return FMLConfig.defaultConfigPath();
    }
}
