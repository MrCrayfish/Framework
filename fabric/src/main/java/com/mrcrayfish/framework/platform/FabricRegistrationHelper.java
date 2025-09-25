package com.mrcrayfish.framework.platform;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mrcrayfish.framework.Constants;
import com.mrcrayfish.framework.Registration;
import com.mrcrayfish.framework.api.FrameworkAPI;
import com.mrcrayfish.framework.api.menu.IMenuData;
import com.mrcrayfish.framework.api.registry.BlockRegistryEntry;
import com.mrcrayfish.framework.api.registry.FrameworkRegistry;
import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.platform.services.IRegistrationHelper;
import com.mrcrayfish.framework.registry.VanillaRegistryProxy;
import com.mrcrayfish.framework.util.ReflectionUtils;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
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
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Author: MrCrayfish
 */
public class FabricRegistrationHelper implements IRegistrationHelper
{
    private final Set<Class<?>> registryClasses = new HashSet<>();
    private boolean loadedRegistryClasses;

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void init()
    {
        // Registers custom registries using Fabric's registry builder
        Services.REGISTRATION.getRegistryObjects(FrameworkRegistry.class).forEach(registry -> {
            Constants.LOG.debug("Registering custom registry: {}", registry.getKey().location());
            var builder = FabricRegistryBuilder.createSimple(registry.getKey());
            if(registry.shouldSync()) builder.attribute(RegistryAttribute.SYNCED);
            registry.setProxy(VanillaRegistryProxy.wrap(builder.buildAndRegister()));
        });

        // Register all entries
        Registration.getAllRegistryEntries().forEach(entry -> {
            entry.register((registryKey, name, valueSupplier) -> {
                Registry registry = BuiltInRegistries.REGISTRY.get(registryKey.location());
                if(registry == null)
                    throw new NullPointerException("Registry not found: " + registryKey);
                Registry.register(registry, name, valueSupplier.get());
            });
        });

        // Special case for block registry entries to register items
        Registration.get(Registries.BLOCK).forEach(entry -> {
            if(entry instanceof BlockRegistryEntry<?, ?> blockEntry) {
                blockEntry.item().ifPresent(item -> Registry.register(BuiltInRegistries.ITEM, entry.getId(), item));
            }
        });
    }

    @Override
    public <T> List<T> getRegistryObjects(Class<T> objectType)
    {
        if(!this.loadedRegistryClasses)
        {
            // Set up reflections to only look in specified packages
            Reflections reflections = new Reflections(new ConfigurationBuilder()
                    .forPackages(this.getScanPackages())
                    .addScanners(Scanners.TypesAnnotated));

            String annotationClassName = RegistryContainer.class.getName();
            String annotationDescriptor = RegistryContainer.class.descriptorString();

            // Check classes annotated with RegistryContainer that they can be loaded
            Map<String, Set<String>> store = reflections.getStore().get(Scanners.TypesAnnotated.name());
            for(String registryClass : store.getOrDefault(annotationClassName, Collections.emptySet()))
            {
                // Get annotation data without loading the class
                Map<String, Object> data = this.readAnnotationData(registryClass, annotationDescriptor);

                // Prevent searching for fields if clientOnly but env is dedicated server
                boolean clientOnly = (boolean) data.getOrDefault("clientOnly", false);
                if(clientOnly && !FrameworkAPI.getEnvironment().isClient())
                    continue;

                // Add as valid class and load the class
                this.registryClasses.add(ReflectionUtils.getClass(registryClass));
            }

            // Finally mark classes as loaded to prevent
            this.loadedRegistryClasses = true;
        }
        return this.registryClasses.stream()
                .flatMap(holderClass -> ReflectionUtils.findPublicStaticObjects(objectType, holderClass).stream())
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

    /**
     * Reads the annotation data of a class without loading the class using ASM.
     *
     * @param className            the fully-qualified name of the class. e.g "java.lang.String"
     * @param annotationDescriptor the descriptor of the annotation
     * @return a map containing the annotation data
     */
    private Map<String, Object> readAnnotationData(String className, String annotationDescriptor)
    {
        Map<String, Object> data = new HashMap<>();
        try(InputStream is = FabricRegistrationHelper.class.getClassLoader().getResourceAsStream(className.replace('.', '/') + ".class"))
        {
            if(is != null)
            {
                ClassReader reader = new ClassReader(is);
                reader.accept(new AnnotationDataCollector(annotationDescriptor, data), ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
            }
        }
        catch(IOException e)
        {
            throw new RuntimeException(e);
        }
        return data;
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

    private static class AnnotationDataCollector extends ClassVisitor
    {
        private final String targetDescriptor;
        private final Map<String, Object> data;

        private AnnotationDataCollector(String targetDescriptor, Map<String, Object> data)
        {
            super(Opcodes.ASM9);
            this.targetDescriptor = targetDescriptor;
            this.data = data;
        }

        @Override
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible)
        {
            if(descriptor.equals(this.targetDescriptor))
            {
                return new AnnotationVisitor(this.api)
                {
                    @Override
                    public void visit(String name, Object value)
                    {
                        AnnotationDataCollector.this.data.put(name, value);
                    }
                };
            }
            return null;
        }
    }
}
