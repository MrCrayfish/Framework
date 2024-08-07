package com.mrcrayfish.framework.api.registry;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mrcrayfish.framework.platform.Services;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.StatFormatter;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.apache.commons.lang3.function.TriFunction;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public sealed class RegistryEntry<T> permits BlockRegistryEntry, CustomStatRegistryEntry
{
    protected final Registry<?> registry;
    protected final ResourceLocation id;
    protected final Supplier<T> supplier;

    RegistryEntry(Registry<?> registry, ResourceLocation id, Supplier<T> supplier)
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

    protected T create()
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

    protected void invalidate()
    {
        this.instance = null;
    }

    @SuppressWarnings("unchecked")
    public void register(IRegisterFunction function)
    {
        function.call((Registry<? super Object>) this.registry, this.id, () -> {
            this.invalidate();
            return this.create();
        });
    }

    public static <T extends Attribute> RegistryEntry<T> attribute(ResourceLocation id, Supplier<T> supplier)
    {
        return new RegistryEntry<>(Registry.ATTRIBUTE, id, supplier);
    }

    public static <T extends Block> RegistryEntry<T> block(ResourceLocation id, Supplier<T> supplier)
    {
        return new BlockRegistryEntry<>(Registry.BLOCK, id, supplier, t -> null);
    }

    public static <T extends Block, E extends BlockItem> RegistryEntry<T> blockWithItem(ResourceLocation id, Supplier<T> supplier)
    {
        return new BlockRegistryEntry<>(Registry.BLOCK, id, supplier, t -> new BlockItem(t, new Item.Properties()));
    }

    public static <T extends Block, E extends BlockItem> RegistryEntry<T> blockWithItem(ResourceLocation id, Supplier<T> supplier, Function<T, E> function)
    {
        return new BlockRegistryEntry<>(Registry.BLOCK, id, supplier, function);
    }

    public static <T extends BlockEntity> RegistryEntry<BlockEntityType<T>> blockEntity(ResourceLocation id, BiFunction<BlockPos, BlockState, T> function, Supplier<Block[]> validBlocksSupplier)
    {
        return new RegistryEntry<>(Registry.BLOCK_ENTITY_TYPE, id, () -> Services.REGISTRATION.createBlockEntityType(function, validBlocksSupplier));
    }

    public static <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>, I extends ArgumentTypeInfo<A, T>> RegistryEntry<I> commandArgumentType(ResourceLocation id, Class<A> argumentTypeClass, Supplier<I> supplier)
    {
        return new RegistryEntry<>(Registry.COMMAND_ARGUMENT_TYPE, id, () -> Services.REGISTRATION.createArgumentTypeInfo(argumentTypeClass, supplier));
    }

    public static RegistryEntry<ResourceLocation> customStat(ResourceLocation id, StatFormatter formatter)
    {
        return new CustomStatRegistryEntry(BuiltInRegistries.CUSTOM_STAT, id, formatter);
    }

    public static <T extends Enchantment> RegistryEntry<T> enchantment(ResourceLocation id, Supplier<T> supplier)
    {
        return new RegistryEntry<>(Registry.ENCHANTMENT, id, supplier);
    }

    public static <T extends EntityType<?>> RegistryEntry<T> entityType(ResourceLocation id, Supplier<T> supplier)
    {
        return new RegistryEntry<>(Registry.ENTITY_TYPE, id, supplier);
    }

    public static <T extends Fluid> RegistryEntry<T> fluid(ResourceLocation id, Supplier<T> supplier)
    {
        return new RegistryEntry<>(Registry.FLUID, id, supplier);
    }

    public static <T extends Item> RegistryEntry<T> item(ResourceLocation id, Supplier<T> supplier)
    {
        return new RegistryEntry<>(Registry.ITEM, id, supplier);
    }

    public static <T extends AbstractContainerMenu> RegistryEntry<MenuType<T>> menuType(ResourceLocation id, BiFunction<Integer, Inventory, T> function)
    {
        return new RegistryEntry<>(Registry.MENU, id, () -> Services.REGISTRATION.createMenuType(function));
    }

    public static <T extends AbstractContainerMenu> RegistryEntry<MenuType<T>> menuTypeWithData(ResourceLocation id, TriFunction<Integer, Inventory, FriendlyByteBuf, T> function)
    {
        return new RegistryEntry<>(Registry.MENU, id, () -> Services.REGISTRATION.createMenuTypeWithData(function));
    }

    public static <T extends MobEffect> RegistryEntry<T> mobEffect(ResourceLocation id, Supplier<T> supplier)
    {
        return new RegistryEntry<>(Registry.MOB_EFFECT, id, supplier);
    }

    public static <T extends ParticleType<?>> RegistryEntry<T> particleType(ResourceLocation id, Supplier<T> supplier)
    {
        return new RegistryEntry<>(Registry.PARTICLE_TYPE, id, supplier);
    }

    public static <T extends Potion> RegistryEntry<T> potion(ResourceLocation id, Supplier<T> supplier)
    {
        return new RegistryEntry<>(Registry.POTION, id, supplier);
    }

    public static <T extends RecipeType<?>> RegistryEntry<T> recipeType(ResourceLocation id, Supplier<T> supplier)
    {
        return new RegistryEntry<>(Registry.RECIPE_TYPE, id, supplier);
    }

    public static <T extends Recipe<?>> RegistryEntry<RecipeType<T>> recipeType(ResourceLocation id)
    {
        return new RegistryEntry<>(BuiltInRegistries.RECIPE_TYPE, id, () -> new RecipeType<>() {
            @Override
            public String toString() {
                return id.getPath();
            }
        });
    }

    public static <T extends RecipeSerializer<?>> RegistryEntry<T> recipeSerializer(ResourceLocation id, Supplier<T> supplier)
    {
        return new RegistryEntry<>(Registry.RECIPE_SERIALIZER, id, supplier);
    }

    public static <T extends SoundEvent> RegistryEntry<T> soundEvent(ResourceLocation id, Function<ResourceLocation, Supplier<T>> supplier)
    {
        return new RegistryEntry<>(Registry.SOUND_EVENT, id, supplier.apply(id));
    }
}
