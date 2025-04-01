package com.mrcrayfish.framework.client.model;

import com.mrcrayfish.framework.Constants;
import com.mrcrayfish.framework.FrameworkData;
import com.mrcrayfish.framework.api.client.model.FrameworkModelBaker;
import com.mrcrayfish.framework.api.client.model.FrameworkModelResource;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Author: MrCrayfish
 */
@ApiStatus.Internal
public final class StandaloneModelManager
{
    private static StandaloneModelManager instance;

    public static StandaloneModelManager getInstance()
    {
        if(instance == null)
        {
            instance = new StandaloneModelManager();
        }
        return instance;
    }

    private final Set<FrameworkModelResource<?>> models = Collections.synchronizedSet(new LinkedHashSet<>());

    private StandaloneModelManager() {}

    public <T> void register(FrameworkModelResource<T> resource)
    {
        if(FrameworkData.isLoaded())
            throw new IllegalStateException("Standalone models can only be registered during client initialization");
        this.models.add(resource);
    }

    public void load(Consumer<FrameworkModelResource<?>> consumer)
    {
        this.models.forEach(resource -> {
            Constants.LOG.debug("Registering standalone model: {}", resource.getLocation());
            consumer.accept(resource);
        });
    }
}
