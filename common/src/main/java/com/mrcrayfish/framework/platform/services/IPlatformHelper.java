package com.mrcrayfish.framework.platform.services;

import com.mrcrayfish.framework.api.Environment;

public interface IPlatformHelper {

    /**
     * @return The current platform
     */
    Platform getPlatform();

    /**
     * Checks if a mod with the given id is loaded.
     *
     * @param modId The mod to check if it is loaded.
     * @return True if the mod is loaded, false otherwise.
     */
    boolean isModLoaded(String modId);

    /**
     * Gets the name of a mod with the given mod id
     * @param modId The mod to get the name for
     * @return The display name of the mod or "Unknown"
     */
    String getModName(String modId);

    /**
     * Check if the game is currently in a development environment.
     *
     * @return True if in a development environment, false otherwise.
     */
    boolean isDevelopmentEnvironment();

    /**
     * Gets the name of the environment type as a string.
     *
     * @return The name of the environment type.
     */
    default String getEnvironmentName()
    {
        return isDevelopmentEnvironment() ? "development" : "production";
    }

    Environment getEnvironment();

    enum Platform
    {
        FORGE, FABRIC;

        public boolean isForge()
        {
            return this == FORGE;
        }

        public boolean isFabric()
        {
            return this == FABRIC;
        }
    }
}