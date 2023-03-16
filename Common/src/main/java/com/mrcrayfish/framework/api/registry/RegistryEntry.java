package com.mrcrayfish.framework.api.registry;

import com.mrcrayfish.framework.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.function.TriFunction;

import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public final class RegistryEntry<T>
{
    private final Registry<?> registry;
    private final ResourceLocation id;
    private final Supplier<T> supplier;

    private RegistryEntry(Registry<?> registry, ResourceLocation id, Supplier<T> supplier)
    {
        this.registry = registry;
        this.id = id;
        this.supplier = supplier;
    }

    private T instance;

    public T get()
    {
        if(this.instance == null)
            throw new IllegalStateException("Entry has not been created yet");
        return this.instance;
    }

    private T create()
    {
        if(this.instance != null)
            throw new IllegalStateException("Entry has already been created");
        this.instance = this.supplier.get();
        return this.instance;
    }

    public Registry<?> getRegistry()
    {
        return this.registry;
    }

    public ResourceLocation getId()
    {
        return this.id;
    }

    @SuppressWarnings("unchecked")
    public void register(IRegisterFunction function)
    {
        function.call((Registry<T>) this.registry, this.id, () -> {
            this.instance = null;
            this.instance = this.create();
            return this.instance;
        });
    }

    public static RegistryEntry<Block> block(ResourceLocation id, Supplier<Block> supplier)
    {
        return new RegistryEntry<>(BuiltInRegistries.BLOCK, id, supplier);
    }

    public static <T extends BlockEntity> RegistryEntry<BlockEntityType<T>> blockEntity(ResourceLocation id, BiFunction<BlockPos, BlockState, T> function, Supplier<Block[]> validBlocksSupplier)
    {
        return new RegistryEntry<>(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, () -> Services.REGISTRATION.createBlockEntityType(function, validBlocksSupplier));
    }

    public static <T extends AbstractContainerMenu> RegistryEntry<MenuType<T>> container(ResourceLocation id, BiFunction<Integer, Inventory, T> function)
    {
        return new RegistryEntry<>(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, () -> Services.REGISTRATION.createMenuType(function));
    }

    public static <T extends AbstractContainerMenu> RegistryEntry<MenuType<T>> containerWithData(ResourceLocation id, TriFunction<Integer, Inventory, FriendlyByteBuf, T> function)
    {
        return new RegistryEntry<>(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, () -> Services.REGISTRATION.createMenuTypeWithData(function));
    }

    public static <T extends Enchantment> RegistryEntry<T> enchantment(ResourceLocation id, Supplier<T> supplier)
    {
        return new RegistryEntry<>(BuiltInRegistries.ENCHANTMENT, id, supplier);
    }

    public static <T extends Item> RegistryEntry<T> item(ResourceLocation id, Supplier<T> supplier)
    {
        return new RegistryEntry<>(BuiltInRegistries.ITEM, id, supplier);
    }
}
