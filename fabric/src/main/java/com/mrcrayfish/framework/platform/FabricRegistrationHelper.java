package com.mrcrayfish.framework.platform;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mrcrayfish.framework.api.menu.IMenuData;
import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
import com.mrcrayfish.framework.platform.services.IRegistrationHelper;
import com.mrcrayfish.framework.util.ReflectionUtils;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.function.TriFunction;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Author: MrCrayfish
 */
public class FabricRegistrationHelper implements IRegistrationHelper
{
    @Override
    public List<RegistryEntry<?>> getAllRegistryEntries()
    {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .forPackages(this.getScanPackages())
                .addScanners(Scanners.TypesAnnotated));
        Set<Class<?>> containerClass = reflections.getTypesAnnotatedWith(RegistryContainer.class);
        return containerClass.stream()
                .map(ReflectionUtils::findRegistryEntriesInClass)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private String[] getScanPackages()
    {
        return FabricLoader.getInstance().getAllMods().stream()
                .map(this::getScanPackages)
                .flatMap(Collection::stream)
                .toArray(String[]::new);
    }

    private List<String> getScanPackages(ModContainer container)
    {
        CustomValue value = container.getMetadata().getCustomValue("framework");
        if(value != null && value.getType() == CustomValue.CvType.OBJECT)
        {
            CustomValue.CvObject frameworkObj = value.getAsObject();
            CustomValue containersValue = frameworkObj.get("containers");
            if(containersValue != null)
            {
                if(containersValue.getType() == CustomValue.CvType.ARRAY)
                {
                    List<String> packages = new ArrayList<>();
                    CustomValue.CvArray packagesArray = containersValue.getAsArray();
                    packagesArray.forEach(packageValue ->
                    {
                        if(packageValue.getType() == CustomValue.CvType.STRING)
                        {
                            packages.add(packageValue.getAsString());
                        }
                    });
                    return packages;
                }
                else if (containersValue.getType() == CustomValue.CvType.STRING)
                {
                    return Collections.singletonList(containersValue.getAsString());
                }
            }
        }
        return Collections.emptyList();
    }

    @Override
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
    public <T extends AbstractContainerMenu, D extends IMenuData<D>> MenuType<T> createMenuTypeWithData(StreamCodec<RegistryFriendlyByteBuf, D> codec, TriFunction<Integer, Inventory, D, T> function)
    {
        return new ExtendedScreenHandlerType<>(function::apply, codec);
    }

    @Override
    public <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>, I extends ArgumentTypeInfo<A, T>> I createArgumentTypeInfo(Class<A> argumentTypeClass, Supplier<I> supplier)
    {
        I instance = supplier.get();
        ArgumentTypeInfos.BY_CLASS.put(argumentTypeClass, instance);
        return instance;
    }

    @Override
    public CreativeModeTab.Builder createCreativeModeTabBuilder()
    {
        return FabricItemGroup.builder();
    }
}
