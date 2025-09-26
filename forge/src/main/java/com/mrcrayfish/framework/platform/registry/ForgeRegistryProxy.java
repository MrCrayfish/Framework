package com.mrcrayfish.framework.platform.registry;

import com.google.common.base.Suppliers;
import com.mrcrayfish.framework.registry.RegistryProxy;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

public final class ForgeRegistryProxy<T> implements RegistryProxy<T>
{
    private final Supplier<IForgeRegistry<T>> supplier;

    @SuppressWarnings("unchecked")
    private ForgeRegistryProxy(Supplier<? extends IForgeRegistry<?>> supplier)
    {
        this.supplier = (Supplier<IForgeRegistry<T>>) Suppliers.memoize(supplier::get);
    }

    @Override
    public boolean containsKey(ResourceLocation id)
    {
        return this.supplier.get().containsKey(id);
    }

    @Override
    public @Nullable T getValue(ResourceLocation id)
    {
        return this.supplier.get().getValue(id);
    }

    @Override
    public Holder<T> getHolder(ResourceLocation id)
    {
        T value = this.supplier.get().getValue(id);
        if(value == null)
            throw new NullPointerException("No value found for " + id);
        return Holder.direct(value);
    }

    @Override
    public Iterable<T> iterable()
    {
        return this.supplier.get();
    }

    public static <T> ForgeRegistryProxy<T> wrap(Supplier<? extends IForgeRegistry<?>> supplier)
    {
        return new ForgeRegistryProxy<>(supplier);
    }
}
