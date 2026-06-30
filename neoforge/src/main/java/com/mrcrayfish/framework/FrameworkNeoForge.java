package com.mrcrayfish.framework;

import com.mrcrayfish.framework.api.registry.BlockRegistryEntry;
import com.mrcrayfish.framework.api.registry.FrameworkRegistry;
import com.mrcrayfish.framework.entity.sync.DataHolder;
import com.mrcrayfish.framework.entity.sync.DataHolderSerializer;
import com.mrcrayfish.framework.entity.sync.SyncedEntityData;
import com.mrcrayfish.framework.event.NeoForgeEvents;
import com.mrcrayfish.framework.platform.Services;
import com.mrcrayfish.framework.platform.network.NeoForgeNetwork;
import com.mrcrayfish.framework.registry.VanillaRegistryProxy;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterConfigurationTasksEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.*;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
@Mod(Constants.MOD_ID)
public class FrameworkNeoForge
{
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Constants.MOD_ID);
    public static final Supplier<AttachmentType<DataHolder>> DATA_HOLDER = ATTACHMENT_TYPES.register("data_holder", () -> AttachmentType.builder(holder -> {
        if(holder instanceof Entity entity && SyncedEntityData.instance().hasSyncedDataKey(entity)) {
            return new DataHolder(entity);
        }
        return DataHolder.EMPTY;
    }).serialize(new DataHolderSerializer()).build());

    public FrameworkNeoForge(IEventBus bus)
    {
        FrameworkSetup.run();
        bus.addListener(this::onCommonSetup);
        bus.addListener(this::onLoadComplete);
        bus.addListener(this::onRegister);
        bus.addListener(this::onRegisterPayloadHandler);
        bus.addListener(this::onRegisterGameConfigurations);
        bus.addListener(this::onRegisterNewRegistry);
        NeoForge.EVENT_BUS.register(new NeoForgeEvents());
        ATTACHMENT_TYPES.register(bus);

        // Allows Framework to be installed on clients and join servers that don't have it.
        // However, if Framework is installed on the server, the client version must match.
        /*ModList.get().getModContainerById(Constants.MOD_ID).ifPresent(container -> {
            String modVersion = container.getModInfo().getVersion().toString();
            ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> modVersion, (remoteVersion, fromServer) -> {
                return fromServer && (remoteVersion == null || remoteVersion.equals(modVersion));
            }));
        });*/
    }

    private void onCommonSetup(FMLCommonSetupEvent event)
    {
        event.enqueueWork(FrameworkSetup::init);
    }

    private void onRegister(RegisterEvent event)
    {
        // Get all RegistryEntry instances and register them into the registries
        Registration.get(event.getRegistryKey()).forEach(entry -> entry.register((registryKey, key, valueSupplier) -> event.register(registryKey, key.identifier(), valueSupplier)));

        // Special case for block registry entries to register items
        if(event.getRegistryKey().equals(Registries.ITEM))
        {
            Registration.get(Registries.BLOCK).forEach(entry ->
            {
                if(entry instanceof BlockRegistryEntry<?, ?> blockEntry)
                {
                    blockEntry.item().ifPresent(item -> event.register(Registries.ITEM, entry.getId(), () -> item));
                }
            });
        }
    }

    private void onLoadComplete(FMLLoadCompleteEvent event)
    {
        FrameworkData.setLoaded();
    }

    private void onRegisterPayloadHandler(RegisterPayloadHandlersEvent event)
    {
        Registration.getNetworks().forEach(network -> {
            PayloadRegistrar registrar = event.registrar(network.id().getNamespace());
            ((NeoForgeNetwork) network).registerPayloads(registrar);
        });
    }

    private void onRegisterGameConfigurations(RegisterConfigurationTasksEvent event)
    {
        Registration.getNetworks().forEach(network -> {
            NeoForgeNetwork neoForgeNetwork = (NeoForgeNetwork) network;
            neoForgeNetwork.getTasks().forEach(f -> event.register(f.apply(neoForgeNetwork, event.getListener())));
        });
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void onRegisterNewRegistry(NewRegistryEvent event)
    {
        // Registers custom registries using NeoForge's event
        Services.REGISTRATION.getRegistryObjects(FrameworkRegistry.class).forEach(registry -> {
            Constants.LOG.debug("Registering custom registry: {}", registry.getKey().identifier());
            RegistryBuilder<?> builder = new RegistryBuilder(registry.getKey()).sync(registry.shouldSync());
            registry.setProxy(VanillaRegistryProxy.wrap(event.create(builder)));
        });
    }
}
