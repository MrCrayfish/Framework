package com.mrcrayfish.framework.api.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public final class BlockRegistryEntry<T extends Block, E extends BlockItem> extends RegistryEntry<T>
{
    private final Function<T, E> itemSupplier;
    private E itemInstance = null;

    BlockRegistryEntry(Registry<?> registry, ResourceLocation id, Supplier<T> blockSupplier, Function<T, E> itemSupplier)
    {
        super(registry, id, blockSupplier);
        this.itemSupplier = itemSupplier;
    }

    @Override
    protected T create()
    {
        T instance = super.create();
        this.itemInstance = this.itemSupplier.apply(instance);
        return instance;
    }

    @Override
    protected void invalidate()
    {
        super.invalidate();
        this.itemInstance = null;
    }

    public Optional<E> item()
    {
        return Optional.ofNullable(this.itemInstance);
    }
}
