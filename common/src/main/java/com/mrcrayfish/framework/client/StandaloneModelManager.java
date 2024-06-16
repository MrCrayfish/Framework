package com.mrcrayfish.framework.client;

import com.mrcrayfish.framework.Constants;
import com.mrcrayfish.framework.FrameworkData;
import net.minecraft.client.resources.model.ModelResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
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

    private final Set<ModelResourceLocation> models = Collections.synchronizedSet(new LinkedHashSet<>());

    private StandaloneModelManager() {}

    public void register(ModelResourceLocation location)
    {
        if(FrameworkData.isLoaded())
            throw new IllegalStateException("Standalone models can only be registered during client initialization");
        this.models.add(location);
    }

    public void load(Consumer<ModelResourceLocation> consumer)
    {
        this.models.forEach(location -> {
            Constants.LOG.debug("Registering standalone model: {}", location);
            consumer.accept(location);
        });
    }
}
