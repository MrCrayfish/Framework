package com.mrcrayfish.framework.platform;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
import com.mrcrayfish.framework.platform.services.IRegistrationHelper;
import com.mrcrayfish.framework.util.ReflectionUtils;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;
import net.minecraftforge.network.IContainerFactory;
import org.apache.commons.lang3.function.TriFunction;
import org.objectweb.asm.Type;

import java.lang.annotation.ElementType;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Author: MrCrayfish
 */
public class ForgeRegistrationHelper implements IRegistrationHelper
{
    public static final Type ENTRY_CONTAINER = Type.getType(RegistryContainer.class);

    @Override
    public List<RegistryEntry<?>> getAllRegistryEntries()
    {
        return ModList.get().getAllScanData().stream()
                .map(ModFileScanData::getAnnotations)
                .flatMap(Collection::stream)
                .filter(a -> ENTRY_CONTAINER.equals(a.annotationType()))
                .filter(a -> a.targetType() == ElementType.TYPE)
                .map(ModFileScanData.AnnotationData::memberName)
                .map(ReflectionUtils::getClass)
                .map(ReflectionUtils::findRegistryEntriesInClass)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings({"ConstantConditions", "NullableProblems"})
    public <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BiFunction<BlockPos, BlockState, T> function, Supplier<Block[]> validBlocksSupplier)
    {
        return BlockEntityType.Builder.of(function::apply, validBlocksSupplier.get()).build(null);
    }

    @Override
    public <T extends AbstractContainerMenu> MenuType<T> createMenuType(BiFunction<Integer, Inventory, T> function)
    {
        return new MenuType<>(function::apply, FeatureFlags.DEFAULT_FLAGS);
    }

    @Override
    public <T extends AbstractContainerMenu> MenuType<T> createMenuTypeWithData(TriFunction<Integer, Inventory, FriendlyByteBuf, T> function)
    {
        return new MenuType<>((IContainerFactory<T>) function::apply, FeatureFlags.DEFAULT_FLAGS);
    }

    @Override
    public <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>, I extends ArgumentTypeInfo<A, T>> I createArgumentTypeInfo(Class<A> argumentTypeClass, Supplier<I> supplier)
    {
        return ArgumentTypeInfos.registerByClass(argumentTypeClass, supplier.get());
    }

    @Override
    public CreativeModeTab.Builder createCreativeModeTabBuilder()
    {
        return CreativeModeTab.builder();
    }
}
