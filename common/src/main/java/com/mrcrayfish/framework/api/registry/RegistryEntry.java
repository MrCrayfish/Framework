package com.mrcrayfish.framework.api.registry;

import com.google.common.base.Suppliers;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mrcrayfish.framework.api.menu.IMenuData;
import com.mrcrayfish.framework.platform.Services;
import com.mrcrayfish.framework.registry.RegisterConsumer;
import com.mrcrayfish.framework.registry.RegistryProxy;
import com.mrcrayfish.framework.registry.VanillaRegistryProxy;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.StatFormatter;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
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
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
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
    protected final ResourceKey<Registry<T>> registryKey;
    protected final Supplier<RegistryProxy<T>> registryProxySupplier;
    protected final ResourceLocation valueId;
    protected final Supplier<T> valueSupplier;

    @SuppressWarnings("unchecked")
    RegistryEntry(Registry<?> registry, ResourceLocation valueId, Supplier<T> valueSupplier)
    {
        this((ResourceKey<Registry<T>>) registry.key(), () -> (RegistryProxy<T>) VanillaRegistryProxy.wrap(registry), valueId, valueSupplier);
    }

    RegistryEntry(FrameworkRegistry<T> registry, ResourceLocation valueId, Supplier<T> valueSupplier)
    {
        this(registry.getKey(), registry::getProxy, valueId, valueSupplier);
    }

    RegistryEntry(ResourceKey<Registry<T>> registryKey, Supplier<RegistryProxy<T>> registryProxySupplier, ResourceLocation valueId, Supplier<T> valueSupplier)
    {
        this.registryKey = registryKey;
        this.registryProxySupplier = Suppliers.memoize(registryProxySupplier::get);
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
        return this.registryKey;
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
        consumer.accept(this.registryKey, this.valueId, () -> value);
        this.holder = this.registryProxySupplier.get().getHolder(this.valueId);
    }

    public static <T> RegistryEntry<T> custom(FrameworkRegistry<T> registry, ResourceLocation id, Supplier<T> supplier)
    {
        return new RegistryEntry<>(registry, id, supplier);
    }

    public static <T extends Attribute> RegistryEntry<T> attribute(ResourceLocation id, Supplier<T> attributeFactory)
    {
        return new RegistryEntry<>(BuiltInRegistries.ATTRIBUTE, id, attributeFactory);
    }

    public static <T extends Block> RegistryEntry<T> block(ResourceLocation id, Function<BlockBehaviour.Properties, T> blockFactory, Supplier<BlockBehaviour.Properties> blockPropertiesFactory)
    {
        return new BlockRegistryEntry<>(BuiltInRegistries.BLOCK, id, () -> {
            BlockBehaviour.Properties properties = blockPropertiesFactory.get();
            return blockFactory.apply(properties.setId(ResourceKey.create(Registries.BLOCK, id)));
        }, t -> null);
    }

    public static <T extends Block> RegistryEntry<T> blockWithItem(ResourceLocation id, Function<BlockBehaviour.Properties, T> blockFactory, Supplier<BlockBehaviour.Properties> blockPropertiesFactory)
    {
        return new BlockRegistryEntry<>(BuiltInRegistries.BLOCK, id, () -> {
            BlockBehaviour.Properties properties = blockPropertiesFactory.get();
            return blockFactory.apply(properties.setId(ResourceKey.create(Registries.BLOCK, id)));
        }, t -> {
            return new BlockItem(t, new Item.Properties().useBlockDescriptionPrefix().setId(ResourceKey.create(Registries.ITEM, id)));
        });
    }

    public static <T extends Block, E extends BlockItem> RegistryEntry<T> blockWithItem(ResourceLocation id, Function<BlockBehaviour.Properties, T> blockFactory, Supplier<BlockBehaviour.Properties> blockPropertiesFactory, BiFunction<T, Item.Properties, E> itemFactory, Supplier<Item.Properties> itemPropertiesFactory)
    {
        return new BlockRegistryEntry<>(BuiltInRegistries.BLOCK, id, () -> {
            BlockBehaviour.Properties blockProperties = blockPropertiesFactory.get();
            return blockFactory.apply(blockProperties.setId(ResourceKey.create(Registries.BLOCK, id)));
        }, t -> {
            Item.Properties itemProperties = itemPropertiesFactory.get();
            return itemFactory.apply(t, itemProperties.useBlockDescriptionPrefix().setId(ResourceKey.create(Registries.ITEM, id)));
        });
    }

    public static <T extends BlockEntity> RegistryEntry<BlockEntityType<T>> blockEntity(ResourceLocation id, BiFunction<BlockPos, BlockState, T> blockEntityFactory, Supplier<Block[]> validBlocks)
    {
        return new RegistryEntry<>(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, () -> Services.REGISTRATION.createBlockEntityType(blockEntityFactory, validBlocks));
    }

    public static <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>, I extends ArgumentTypeInfo<A, T>> RegistryEntry<I> commandArgumentType(ResourceLocation id, Class<A> argumentTypeClass, Supplier<I> argumentTypeFactory)
    {
        return new RegistryEntry<>(BuiltInRegistries.COMMAND_ARGUMENT_TYPE, id, () -> Services.REGISTRATION.createArgumentTypeInfo(argumentTypeClass, argumentTypeFactory));
    }

    public static RegistryEntry<CreativeModeTab> creativeModeTab(ResourceLocation id, Consumer<CreativeModeTab.Builder> builderConsumer)
    {
        return new RegistryEntry<>(BuiltInRegistries.CREATIVE_MODE_TAB, id, () -> {
            CreativeModeTab.Builder builder = Services.REGISTRATION.createCreativeModeTabBuilder();
            builderConsumer.accept(builder);
            return builder.build();
        });
    }

    public static RegistryEntry<ResourceLocation> customStat(ResourceLocation id, StatFormatter formatter)
    {
        return new CustomStatRegistryEntry(BuiltInRegistries.CUSTOM_STAT, id, formatter);
    }

    public static <T> RegistryEntry<DataComponentType<T>> dataComponentType(ResourceLocation id, UnaryOperator<DataComponentType.Builder<T>> builderOperator)
    {
        return new RegistryEntry<>(BuiltInRegistries.DATA_COMPONENT_TYPE, id, () -> builderOperator.apply(DataComponentType.builder()).build());
    }

    public static <T> RegistryEntry<DataComponentType<T>> enchantmentEffectComponentType(ResourceLocation id, UnaryOperator<DataComponentType.Builder<T>> builderOperator)
    {
        return new RegistryEntry<>(BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, id, () -> builderOperator.apply(DataComponentType.builder()).build());
    }

    public static <T extends Entity> RegistryEntry<EntityType<T>> entityType(ResourceLocation id, Supplier<EntityType.Builder<T>> entityTypeFactory)
    {
        return new RegistryEntry<>(BuiltInRegistries.ENTITY_TYPE, id, () -> {
            return entityTypeFactory.get().build(ResourceKey.create(Registries.ENTITY_TYPE, id));
        });
    }

    public static <T extends Fluid> RegistryEntry<T> fluid(ResourceLocation id, Supplier<T> fluidFactory)
    {
        return new RegistryEntry<>(BuiltInRegistries.FLUID, id, fluidFactory);
    }

    public static <T extends Item> RegistryEntry<T> item(ResourceLocation id, Function<Item.Properties, T> itemFactory, Supplier<Item.Properties> itemPropertiesFactory)
    {
        return new RegistryEntry<>(BuiltInRegistries.ITEM, id, () -> {
            Item.Properties itemProperties = itemPropertiesFactory.get();
            return itemFactory.apply(itemProperties.setId(ResourceKey.create(Registries.ITEM, id)));
        });
    }

    public static <T extends AbstractContainerMenu> RegistryEntry<MenuType<T>> menuType(ResourceLocation id, BiFunction<Integer, Inventory, T> menuFactory)
    {
        return new RegistryEntry<>(BuiltInRegistries.MENU, id, () -> Services.REGISTRATION.createMenuType(menuFactory));
    }

    public static <T extends AbstractContainerMenu, D extends IMenuData<D>> RegistryEntry<MenuType<T>> menuTypeWithData(ResourceLocation id, StreamCodec<RegistryFriendlyByteBuf, D> dataCodec, TriFunction<Integer, Inventory, D, T> menuFactory)
    {
        return new RegistryEntry<>(BuiltInRegistries.MENU, id, () -> Services.REGISTRATION.createMenuTypeWithData(dataCodec, menuFactory));
    }

    public static <T extends MobEffect> RegistryEntry<T> mobEffect(ResourceLocation id, Supplier<T> mobEffectFactory)
    {
        return new RegistryEntry<>(BuiltInRegistries.MOB_EFFECT, id, mobEffectFactory);
    }

    public static <T extends ParticleType<?>> RegistryEntry<T> particleType(ResourceLocation id, Supplier<T> particleTypeFactory)
    {
        return new RegistryEntry<>(BuiltInRegistries.PARTICLE_TYPE, id, particleTypeFactory);
    }

    public static <T extends Potion> RegistryEntry<T> potion(ResourceLocation id, Supplier<T> potionFactory)
    {
        return new RegistryEntry<>(BuiltInRegistries.POTION, id, potionFactory);
    }

    public static <T extends RecipeDisplay, D extends RecipeDisplay.Type<T>> RegistryEntry<D> recipeDisplay(ResourceLocation id, Supplier<D> recipeDisplayFactory)
    {
        return new RegistryEntry<>(BuiltInRegistries.RECIPE_DISPLAY, id, recipeDisplayFactory);
    }

    public static RegistryEntry<RecipeBookCategory> recipeBookCategory(ResourceLocation id)
    {
        return new RegistryEntry<>(BuiltInRegistries.RECIPE_BOOK_CATEGORY, id, RecipeBookCategory::new);
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

    public static <T extends RecipeSerializer<?>> RegistryEntry<T> recipeSerializer(ResourceLocation id, Supplier<T> recipeSerializerFactory)
    {
        return new RegistryEntry<>(BuiltInRegistries.RECIPE_SERIALIZER, id, recipeSerializerFactory);
    }

    public static <T extends SoundEvent> RegistryEntry<T> soundEvent(ResourceLocation id, Function<ResourceLocation, Supplier<T>> soundEventFactory)
    {
        return new RegistryEntry<>(BuiltInRegistries.SOUND_EVENT, id, soundEventFactory.apply(id));
    }
}
