package com.mrcrayfish.framework.platform.services;

import com.mrcrayfish.framework.api.config.FrameworkConfig;
import org.apache.commons.lang3.tuple.Pair;

import java.nio.file.Path;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public interface IConfigHelper
{
    List<Pair<FrameworkConfig, Object>> getAllFrameworkConfigs();

    Path getConfigPath();

    Path getGamePath();

    String getDefaultConfigPath();
}
