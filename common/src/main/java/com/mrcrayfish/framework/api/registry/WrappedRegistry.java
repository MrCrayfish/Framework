package com.mrcrayfish.framework.api.registry;

import com.mrcrayfish.framework.registry.RegistryProxy;
import com.mrcrayfish.framework.registry.VanillaRegistryProxy;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

/**
 * An abstraction class that wraps over platform specific registries. Fabric and NeoForge use vanilla
 * registries, while Forge uses its own custom IForgeRegistry. An abstraction was needed to interact
 * with different types of registries, not just vanilla. This class provides a few common methods
 * to interact with the underlying registry.
 * <p>
 * It should be noted that custom registries might not be initialized yet, so calling methods too
 * early may result in an exception. To be safe, it's best to interact will this class after the game
 * has started.
 *
 * @param <T> The type of object this registry holds
 */
public class WrappedRegistry<T> implements Iterable<T>
{
    private final ResourceKey<Registry<T>> key;
    private @Nullable RegistryProxy<T> proxy;

    public WrappedRegistry(ResourceKey<Registry<T>> key)
    {
        this.key = key;
    }

    /**
     * @return The ResourceKey of this registry
     */
    public ResourceKey<Registry<T>> getKey()
    {
        return this.key;
    }

    /**
     * Determines if this registry contains a value with the given id. Warning, this method may
     * throw an IllegalStateException if called too early, especially if it's a custom registry.
     * Each modloader has a different stage during the initialization process when custom registries
     * are registered. To be safe, only access this method after the game or server has completed
     * started.
     *
     * @param id a resource location of the value
     * @return True if a match was found
     */
    public boolean containsKey(ResourceLocation id)
    {
        return this.getProxy().containsKey(id);
    }

    /**
     * Gets the value associated with the given id or null if it doesn't exist.
     *
     * @param id the id of the registered value
     * @return T or null if no value matching the id
     */
    @Nullable
    public T getValue(ResourceLocation id)
    {
        return this.getProxy().getValue(id);
    }

    /**
     * An iterator that goes over all the objects in this registry. Warning, this method may
     * throw an IllegalStateException if called too early, especially if it's a custom registry.
     * Each modloader has a different stage during the initialization process when custom registries
     * are registered. To be safe, only access this method after the game or server has completed
     * started.

     * @return An iterator that goes over all the values in this registry
     */
    @NotNull
    @Override
    public Iterator<T> iterator()
    {
        return this.getProxy().iterable().iterator();
    }

    @ApiStatus.Internal
    RegistryProxy<T> getProxy()
    {
        if(this.proxy == null)
            throw new IllegalStateException("Registry not created yet");
        return this.proxy;
    }

    @ApiStatus.Internal
    @SuppressWarnings("unchecked")
    public void setProxy(RegistryProxy<?> registry)
    {
        this.proxy = (RegistryProxy<T>) registry;
    }

    @ApiStatus.Internal
    public static <T> WrappedRegistry<T> wrapVanilla(Registry<T> registry)
    {
        ResourceKey<Registry<T>> key = ResourceKey.createRegistryKey(registry.key().location());
        WrappedRegistry<T> wrapped = new WrappedRegistry<>(key);
        wrapped.proxy = VanillaRegistryProxy.wrap(registry);
        return wrapped;
    }
}
