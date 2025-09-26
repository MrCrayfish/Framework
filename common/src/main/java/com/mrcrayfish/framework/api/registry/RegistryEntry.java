package com.mrcrayfish.framework.api.registry;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mrcrayfish.framework.api.menu.IMenuData;
import com.mrcrayfish.framework.platform.Services;
import com.mrcrayfish.framework.registry.RegisterConsumer;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
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
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.*;

/**
 * Author: MrCrayfish
 */
public sealed class RegistryEntry<T> permits BlockRegistryEntry, CustomStatRegistryEntry
{
    protected final WrappedRegistry<T> registry;
    protected final ResourceLocation valueId;
    protected final Supplier<T> valueSupplier;

    @SuppressWarnings("unchecked")
    RegistryEntry(Registry<?> registry, ResourceLocation valueId, Supplier<T> valueSupplier)
    {
        this((WrappedRegistry<T>) WrappedRegistry.wrapVanilla(registry), valueId, valueSupplier);
    }

    RegistryEntry(WrappedRegistry<T> registry, ResourceLocation valueId, Supplier<T> valueSupplier)
    {
        this.registry = registry;
        this.valueId = valueId;
        this.valueSupplier = valueSupplier;
    }

    private T instance;
    private Holder<T> holder;

    public T get()
    {
        if(this.instance == null)
            throw new IllegalStateException("Entry has not been created yet");
        return this.instance;
    }

    public Holder<T> holder()
    {
        if(this.holder == null)
            throw new IllegalStateException("Entry has not been created yet");
        return this.holder;
    }

    protected T create()
    {
        if(this.instance != null)
            throw new IllegalStateException("Entry has already been created");
        this.instance = this.valueSupplier.get();
        return this.instance;
    }

    public ResourceKey<Registry<T>> getRegistryKey()
    {
        return this.registry.getKey();
    }

    public ResourceLocation getId()
    {
        return this.valueId;
    }

    protected void invalidate()
    {
        this.instance = null;
        this.holder = null;
    }

    @ApiStatus.Internal
    public void register(RegisterConsumer<T> consumer)
    {
        this.invalidate();
        T value = this.create();
        consumer.accept(this.registry.getKey(), this.valueId, () -> value);
        this.holder = this.registry.getProxy().getHolder(this.valueId);
    }

    /**
     * Creates a registry entry using a custom registry.
     *
     * <p>Please note that the object type of the FrameworkRegistry argument is a wildcard and
     * unrestricted. This is to allow this method to be more flexible in cases where the type of the
     * supplied value does not strictly match the registry type.</p>
     *
     * <p>For example, a registry may be created as {@code FrameworkRegistry<MyObject<?>>} but the
     * supplied object type is {@code MyObject<String>}. The registry is expecting {@code MyObject<?>},
     * but it would not be possible to add {@code MyObject<String>} if this method expected the
     * supplied value type to exactly match the type of the registry. It is more useful that the type
     * of the {@code RegistryEntry} preserves its type, so that when calling {@code RegistryEntry#get()}
     * it will return {@code MyObject<String>}, rather than a wildcard.</p>
     *
     * @param registry the registry to bound this entry to
     * @param id       a resource location representing the id of the value. Must be unique
     * @param supplier a supplier which provides an instance of the value
     * @param <T>      the type of the object being registered
     * @return a registry entry that represents the registered object
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> RegistryEntry<T> custom(FrameworkRegistry registry, ResourceLocation id, Supplier<T> supplier)
    {
        return new RegistryEntry<>(registry, id, supplier);
    }

    public static <T extends Attribute> RegistryEntry<T> attribute(ResourceLocation id, Supplier<T> supplier)
    {
        return new RegistryEntry<>(BuiltInRegistries.ATTRIBUTE, id, supplier);
    }

    public static <T extends Block> RegistryEntry<T> block(ResourceLocation id, Supplier<T> supplier)
    {
        return new BlockRegistryEntry<>(BuiltInRegistries.BLOCK, id, supplier, t -> null);
    }

    public static <T extends Block> RegistryEntry<T> blockWithItem(ResourceLocation id, Supplier<T> supplier)
    {
        return new BlockRegistryEntry<>(BuiltInRegistries.BLOCK, id, supplier, t -> new BlockItem(t, new Item.Properties()));
    }

    public static <T extends Block, E extends BlockItem> RegistryEntry<T> blockWithItem(ResourceLocation id, Supplier<T> supplier, Function<T, E> function)
    {
        return new BlockRegistryEntry<>(BuiltInRegistries.BLOCK, id, supplier, function);
    }

    public static <T extends BlockEntity> RegistryEntry<BlockEntityType<T>> blockEntity(ResourceLocation id, BiFunction<BlockPos, BlockState, T> function, Supplier<Block[]> validBlocksSupplier)
    {
        return new RegistryEntry<>(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, () -> Services.REGISTRATION.createBlockEntityType(function, validBlocksSupplier));
    }

    public static <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>, I extends ArgumentTypeInfo<A, T>> RegistryEntry<I> commandArgumentType(ResourceLocation id, Class<A> argumentTypeClass, Supplier<I> supplier)
    {
        return new RegistryEntry<>(BuiltInRegistries.COMMAND_ARGUMENT_TYPE, id, () -> Services.REGISTRATION.createArgumentTypeInfo(argumentTypeClass, supplier));
    }

    public static RegistryEntry<CreativeModeTab> creativeModeTab(ResourceLocation id, Consumer<CreativeModeTab.Builder> consumer)
    {
        return new RegistryEntry<>(BuiltInRegistries.CREATIVE_MODE_TAB, id, () -> {
            CreativeModeTab.Builder builder = Services.REGISTRATION.createCreativeModeTabBuilder();
            consumer.accept(builder);
            return builder.build();
        });
    }

    public static RegistryEntry<ResourceLocation> customStat(ResourceLocation id, StatFormatter formatter)
    {
        return new CustomStatRegistryEntry(BuiltInRegistries.CUSTOM_STAT, id, formatter);
    }

    public static <T> RegistryEntry<DataComponentType<T>> dataComponentType(ResourceLocation id, UnaryOperator<DataComponentType.Builder<T>> operator)
    {
        return new RegistryEntry<>(BuiltInRegistries.DATA_COMPONENT_TYPE, id, () -> operator.apply(DataComponentType.builder()).build());
    }

    public static <T> RegistryEntry<DataComponentType<T>> enchantmentEffectComponentType(ResourceLocation id, UnaryOperator<DataComponentType.Builder<T>> operator)
    {
        return new RegistryEntry<>(BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, id, () -> operator.apply(DataComponentType.builder()).build());
    }

    public static <T extends EntityType<?>> RegistryEntry<T> entityType(ResourceLocation id, Supplier<T> supplier)
    {
        return new RegistryEntry<>(BuiltInRegistries.ENTITY_TYPE, id, supplier);
    }

    public static <T extends Fluid> RegistryEntry<T> fluid(ResourceLocation id, Supplier<T> supplier)
    {
        return new RegistryEntry<>(BuiltInRegistries.FLUID, id, supplier);
    }

    public static <T extends Item> RegistryEntry<T> item(ResourceLocation id, Supplier<T> supplier)
    {
        return new RegistryEntry<>(BuiltInRegistries.ITEM, id, supplier);
    }

    public static <T extends AbstractContainerMenu> RegistryEntry<MenuType<T>> menuType(ResourceLocation id, BiFunction<Integer, Inventory, T> function)
    {
        return new RegistryEntry<>(BuiltInRegistries.MENU, id, () -> Services.REGISTRATION.createMenuType(function));
    }

    public static <T extends AbstractContainerMenu, D extends IMenuData<D>> RegistryEntry<MenuType<T>> menuTypeWithData(ResourceLocation id, StreamCodec<RegistryFriendlyByteBuf, D> codec, TriFunction<Integer, Inventory, D, T> function)
    {
        return new RegistryEntry<>(BuiltInRegistries.MENU, id, () -> Services.REGISTRATION.createMenuTypeWithData(codec, function));
    }

    public static <T extends MobEffect> RegistryEntry<T> mobEffect(ResourceLocation id, Supplier<T> supplier)
    {
        return new RegistryEntry<>(BuiltInRegistries.MOB_EFFECT, id, supplier);
    }

    public static <T extends ParticleType<?>> RegistryEntry<T> particleType(ResourceLocation id, Supplier<T> supplier)
    {
        return new RegistryEntry<>(BuiltInRegistries.PARTICLE_TYPE, id, supplier);
    }

    public static <T extends Potion> RegistryEntry<T> potion(ResourceLocation id, Supplier<T> supplier)
    {
        return new RegistryEntry<>(BuiltInRegistries.POTION, id, supplier);
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
        return new RegistryEntry<>(BuiltInRegistries.RECIPE_SERIALIZER, id, supplier);
    }

    public static <T extends SoundEvent> RegistryEntry<T> soundEvent(ResourceLocation id, Function<ResourceLocation, Supplier<T>> supplier)
    {
        return new RegistryEntry<>(BuiltInRegistries.SOUND_EVENT, id, supplier.apply(id));
    }
}
