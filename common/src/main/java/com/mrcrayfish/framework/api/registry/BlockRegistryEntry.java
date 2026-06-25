package com.mrcrayfish.framework.api.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public final class BlockRegistryEntry<T extends Block, E extends BlockItem> extends RegistryEntry<T>
{
    private final Function<T, E> itemSupplier;
    private final ResourceKey<@NotNull Item> itemKey;
    private E itemInstance = null;

    BlockRegistryEntry(Registry<?> registry, Identifier id, Supplier<T> blockSupplier, Function<T, E> itemSupplier)
    {
        super(registry, id, blockSupplier);
        this.itemSupplier = itemSupplier;
        this.itemKey = ResourceKey.create(Registries.ITEM, id);
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

    public ResourceKey<@NotNull T> blockKey()
    {
        return this.valueKey;
    }

    public ResourceKey<@NotNull Item> itemKey()
    {
        return this.itemKey;
    }

    public Optional<E> item()
    {
        return Optional.ofNullable(this.itemInstance);
    }
}
