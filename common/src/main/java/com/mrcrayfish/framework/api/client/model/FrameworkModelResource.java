package com.mrcrayfish.framework.api.client.model;

import com.mrcrayfish.framework.api.client.FrameworkClientAPI;
import com.mrcrayfish.framework.platform.ClientServices;
import net.minecraft.client.renderer.block.model.SimpleModelWrapper;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public abstract class FrameworkModelResource<T>
{
    /**
     * Baker for vanilla's SimpleModelWrapper. Use {@link #createVanilla(ResourceLocation)} to create a resource.
     */
    public static final FrameworkModelBaker<SimpleModelWrapper> SIMPLE_MODEL_BAKER = (location, model, baker) -> {
        return SimpleModelWrapper.bake(baker, location, Variant.SimpleModelState.DEFAULT.asModelState());
    };

    private final ResourceLocation location;
    private final FrameworkModelBaker<T> baker;
    private T cachedModel;

    FrameworkModelResource(ResourceLocation location, FrameworkModelBaker<T> baker)
    {
        this.location = location;
        this.baker = baker;
    }

    /**
     * @return The location of the resource
     */
    public final ResourceLocation getLocation()
    {
        return this.location;
    }

    /**
     * @return The baker for the model
     */
    public final FrameworkModelBaker<T> getBaker()
    {
        return this.baker;
    }

    @Override
    public boolean equals(Object o)
    {
        if(o == null || getClass() != o.getClass())
            return false;
        FrameworkModelResource<?> that = (FrameworkModelResource<?>) o;
        return this.location.equals(that.location);
    }

    @Override
    public int hashCode()
    {
        return this.location.hashCode();
    }

    /**
     * Gets the standalone model for this model resource. The resource must be registered with
     * {@link com.mrcrayfish.framework.api.client.FrameworkClientAPI#registerStandaloneModel(FrameworkModelResource)}
     * in order for the model to be loaded, otherwise this method will always return null.
     *
     * @return The baked standalone model implementation or null if not found
     */
    @Nullable
    public T getModel()
    {
        if(this.cachedModel == null)
        {
            // Internal code, do not call services directly since they may change at any time.
            this.cachedModel = ClientServices.CLIENT.getStandaloneModel(this);
        }
        return this.cachedModel;
    }

    /**
     * Clears the cached model reference
     */
    public void clearCache()
    {
        this.cachedModel = null;
    }

    /**
     * Creates a definition for a standalone model resource and is baked by Framework's FrameworkStandaloneModel. This
     * gives access to the {@link com.mrcrayfish.framework.client.model.IOpenModel} interface and allows to retrieve
     * any custom data. {@link FrameworkModelResource} must be registered using {@link FrameworkClientAPI#registerStandaloneModel(FrameworkModelResource)}
     * in order for the model to be loaded. Models can be then be retrieved using {@link FrameworkModelResource#getModel()}
     * once the game has finished loading resources.

     * @param location the location of the model. The path starts from the models directory.
     * @return a new model resource holding the definition
     */
    public static FrameworkModelResource<FrameworkStandaloneModel> create(ResourceLocation location)
    {
        // Internal code, do not call services directly since they may change at any time.
        return createCustom(location, FrameworkStandaloneModel.BAKER);
    }

    /**
     * Creates a definition for a standalone model resource and is baked by vanilla's SimpleModelWrapper, which is more
     * appropriate for universal use. {@link FrameworkModelResource} must be registered using {@link FrameworkClientAPI#registerStandaloneModel(FrameworkModelResource)}
     * in order for the model to be loaded. Models can be then be retrieved using {@link FrameworkModelResource#getModel()}
     * once the game has finished loading resources.
     *
     * @param location the location of the model. The path starts from the models directory.
     * @return a new model resource holding the definition
     */
    public static FrameworkModelResource<SimpleModelWrapper> createVanilla(ResourceLocation location)
    {
        // Internal code, do not call services directly since they may change at any time.
        return createCustom(location, SIMPLE_MODEL_BAKER);
    }

    /**
     * Creates a definition for a model resource which contains the model location and the baker used to bake the
     * model into the provided type T. {@link FrameworkModelResource} must be registered using
     * {@link FrameworkClientAPI#registerStandaloneModel(FrameworkModelResource)} in order for the model to be loaded. Models can be
     * then be retrieved using {@link FrameworkModelResource#getModel()} once the game has finished loading resources.
     *
     * @param location the location of the model. The path starts from the models directory.
     * @param baker    the baker responsible to bake into the provided type T
     * @param <T>      the implementation type of the baked model
     * @return a new model resource holding the definition and baker
     */
    public static <T> FrameworkModelResource<T> createCustom(ResourceLocation location, FrameworkModelBaker<T> baker)
    {
        // Internal code, do not call services directly since they may change at any time.
        return ClientServices.CLIENT.createModelResource(location, baker);
    }
}
