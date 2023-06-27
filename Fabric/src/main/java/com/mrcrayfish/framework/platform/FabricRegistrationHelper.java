package com.mrcrayfish.framework.platform;

import com.google.common.reflect.ClassPath;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mrcrayfish.framework.Constants;
import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
import com.mrcrayfish.framework.platform.services.IRegistrationHelper;
import com.mrcrayfish.framework.util.ReflectionUtils;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
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

import java.io.IOException;
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
        try
        {
            Constants.LOG.debug("Scanning for registry containers");
            long time = System.currentTimeMillis();
            List<String> scanPackages = this.getScanPackages();
            ClassLoader loader = FabricRegistrationHelper.class.getClassLoader();
            Set<Class<?>> containerClasses = ClassPath.from(loader).getAllClasses()
                    .stream()
                    .filter(info -> scanPackages.contains(info.getPackageName()))
                    .map(info -> this.loadClass(info.getName(), loader))
                    .filter(clazz -> clazz.getDeclaredAnnotation(RegistryContainer.class) != null)
                    .collect(Collectors.toSet());
            Constants.LOG.debug("Found {} registry container(s) in {} milliseconds", containerClasses.size(), System.currentTimeMillis() - time);
            return containerClasses.stream()
                    .map(ReflectionUtils::findRegistryEntriesInClass)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
        }
        catch(IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private Class<?> loadClass(String className, ClassLoader loader)
    {
        try
        {
            return Class.forName(className, false, loader);
        }
        catch(ClassNotFoundException e)
        {
            throw new RuntimeException(e);
        }
    }

    private List<String> getScanPackages()
    {
        return FabricLoader.getInstance().getAllMods().stream()
                .map(this::getScanPackages)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
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
        return new MenuType<>(function::apply);
    }

    @Override
    public <T extends AbstractContainerMenu> MenuType<T> createMenuTypeWithData(TriFunction<Integer, Inventory, FriendlyByteBuf, T> function)
    {
        return new ExtendedScreenHandlerType<>(function::apply);
    }

    @Override
    public <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>, I extends ArgumentTypeInfo<A, T>> I createArgumentTypeInfo(Class<A> argumentTypeClass, Supplier<I> supplier)
    {
        I instance = supplier.get();
        ArgumentTypeInfos.BY_CLASS.put(argumentTypeClass, instance);
        return instance;
    }
}
