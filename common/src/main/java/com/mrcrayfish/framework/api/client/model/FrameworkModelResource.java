package com.mrcrayfish.framework.api.client.model;

import com.mrcrayfish.framework.platform.ClientServices;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.SimpleModelWrapper;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

public abstract class FrameworkModelResource<T>
{
    private final Identifier location;
    private final FrameworkModelBaker<T> baker;
    private T cachedModel;

    FrameworkModelResource(Identifier location, FrameworkModelBaker<T> baker)
    {
        this.location = location;
        this.baker = baker;
    }

    /**
     * @return The location of the resource
     */
    public final Identifier getLocation()
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
     * @return The baked standalone model or null if not found
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
     * any custom data.
     * <p>
     * {@link FrameworkModelResource} must be registered using a {@link com.mrcrayfish.framework.api.registry.RegistryContainer} and
     * will allow all custom models to be registered automatically into Framework (see code below). Models can be then be retrieved
     * using {@link FrameworkModelResource#getModel()} once the game has finished loading resources.
     *
     * <pre><code>
     *      &#64;RegistryContainer
     *      public final class CustomModels {
     *          public static final FrameworkModelResource<FrameworkBakedModel> MY_CUSTOM_MODEL = FrameworkModelResource.create(Identifier.fromNamespaceAndPath("mymod", "custom/my_custom_model"));
     *      }
     * </code></pre>
     *
     * @param location the location of the model. The path starts from the models directory.
     * @return a new model resource holding the definition
     */
    public static FrameworkModelResource<FrameworkBakedModel> create(Identifier location)
    {
        // Internal code, do not call services directly since they may change at any time.
        return createCustom(location, FrameworkBakedModel.BAKER);
    }

    /**
     * Creates a definition for a standalone model resource and is baked by vanilla's SimpleModelWrapper, which is more
     * appropriate for universal use.
     * <p>
     * {@link FrameworkModelResource} must be registered using a {@link com.mrcrayfish.framework.api.registry.RegistryContainer} and
     * will allow all custom models to be registered automatically into Framework (see code below). Models can be then be retrieved
     * using {@link FrameworkModelResource#getModel()} once the game has finished loading resources.
     *
     * <pre><code>
     *      &#64;RegistryContainer
     *      public final class CustomModels {
     *          public static final FrameworkModelResource<SimpleModelWrapper> MY_CUSTOM_MODEL = FrameworkModelResource.createVanilla(Identifier.fromNamespaceAndPath("mymod", "custom/my_custom_model"));
     *      }
     * </code></pre>
     *
     * @param location the location of the model. The path starts from the models directory.
     * @return a new model resource holding the definition
     */
    public static FrameworkModelResource<BlockModelPart> createVanilla(Identifier location)
    {
        // Internal code, do not call services directly since they may change at any time.
        return createCustom(location, (model, baker) -> {
            return SimpleModelWrapper.bake(baker, location, Variant.SimpleModelState.DEFAULT.asModelState());
        });
    }

    /**
     * Creates a definition for a model resource which contains the model location and the baker used to bake the
     * model into the provided type T.
     * <p>
     * {@link FrameworkModelResource} must be registered using a {@link com.mrcrayfish.framework.api.registry.RegistryContainer} and
     * will allow all custom models to be registered automatically into Framework (see code below). Models can be then be retrieved
     * using {@link FrameworkModelResource#getModel()} once the game has finished loading resources.
     *
     * <pre><code>
     *      &#64;RegistryContainer
     *      public final class CustomModels {
     *          public static final FrameworkModelResource<T> MY_CUSTOM_MODEL = FrameworkModelResource.createCustom(Identifier.fromNamespaceAndPath("mymod", "custom/my_custom_model"), (model, baker) -> {
     *              // Your baking code
     *              return T;
     *          });
     *      }
     * </code></pre>
     *
     * @param location the location of the model. The path starts from the models directory.
     * @param baker    the baker responsible to bake into the provided type T
     * @param <T>      the implementation type of the baked model
     * @return a new model resource holding the definition and baker
     */
    public static <T> FrameworkModelResource<T> createCustom(Identifier location, FrameworkModelBaker<T> baker)
    {
        // Internal code, do not call services directly since they may change at any time.
        return ClientServices.CLIENT.createModelResource(location, baker);
    }
}
