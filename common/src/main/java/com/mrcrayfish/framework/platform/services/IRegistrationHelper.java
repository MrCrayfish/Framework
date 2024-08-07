package com.mrcrayfish.framework.platform.services;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.function.TriFunction;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public interface IRegistrationHelper
{
    List<RegistryEntry<?>> getAllRegistryEntries();

    <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BiFunction<BlockPos, BlockState, T> function, Supplier<Block[]> validBlocksSupplier);

    <T extends AbstractContainerMenu> MenuType<T> createMenuType(BiFunction<Integer, Inventory, T> function);

    <T extends AbstractContainerMenu> MenuType<T> createMenuTypeWithData(TriFunction<Integer, Inventory, FriendlyByteBuf, T> function);

    <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>, I extends ArgumentTypeInfo<A, T>> I createArgumentTypeInfo(Class<A> argumentTypeClass, Supplier<I> supplier);
}
