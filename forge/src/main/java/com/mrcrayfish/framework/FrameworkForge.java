package com.mrcrayfish.framework;

import com.mrcrayfish.framework.api.registry.BlockRegistryEntry;
import com.mrcrayfish.framework.api.registry.IRegisterFunction;
import com.mrcrayfish.framework.client.ClientFrameworkForge;
import com.mrcrayfish.framework.entity.sync.ForgeSyncedEntityDataHandler;
import com.mrcrayfish.framework.event.ForgeEvents;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
@Mod(Constants.MOD_ID)
public class FrameworkForge
{
    public static final Logger LOGGER = LogManager.getLogger("Framework");

    public FrameworkForge()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::onCommonSetup);
        bus.addListener(this::onLoadComplete);
        bus.addListener(this::onRegister);
        bus.addListener(ForgeSyncedEntityDataHandler::registerCapabilities);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            bus.addListener(this::onClientSetup);
            bus.addListener(ClientFrameworkForge::registerKeyMappings);
            bus.addListener(ClientFrameworkForge::registerReloadListener);
        });
        FrameworkSetup.run();
        MinecraftForge.EVENT_BUS.register(new ForgeEvents());

        // Allows Framework to be installed on clients and join servers that don't have it.
        // However, if Framework is installed on the server, the client version must match.
        ModList.get().getModContainerById(Constants.MOD_ID).ifPresent(container -> {
            String modVersion = container.getModInfo().getVersion().toString();
            ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> modVersion, (remoteVersion, fromServer) -> {
                return fromServer && (remoteVersion == null || remoteVersion.equals(modVersion));
            }));
        });
    }

    private void onCommonSetup(FMLCommonSetupEvent event)
    {
        event.enqueueWork(FrameworkSetup::init);
    }

    private void onClientSetup(FMLClientSetupEvent event)
    {
        event.enqueueWork(ClientFrameworkForge::init);
    }

    private void onRegister(RegisterEvent event)
    {
        Registration.get(event.getRegistryKey()).forEach(entry -> entry.register(new IRegisterFunction()
        {
            @Override
            public <T> void call(Registry<T> registry, ResourceLocation name, Supplier<T> supplier)
            {
                event.register(registry.key(), name, supplier);
            }
        }));

        // Special case for block registry entries to register items
        if(event.getRegistryKey().equals(Registry.ITEM_REGISTRY))
        {
            Registration.get(Registry.BLOCK_REGISTRY).forEach(entry ->
            {
                if(entry instanceof BlockRegistryEntry<?, ?> blockEntry)
                {
                    blockEntry.item().ifPresent(item -> event.register(Registry.ITEM_REGISTRY, entry.getId(), () -> item));
                }
            });
        }
    }

    private void onLoadComplete(FMLLoadCompleteEvent event)
    {
        FrameworkData.setLoaded();
    }
}
