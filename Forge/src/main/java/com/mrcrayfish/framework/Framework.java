package com.mrcrayfish.framework;

import com.mrcrayfish.framework.api.registry.IRegisterFunction;
import com.mrcrayfish.framework.client.ClientHandler;
import com.mrcrayfish.framework.entity.sync.ForgeSyncedEntityDataHandler;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
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
public class Framework
{
    public static final Logger LOGGER = LogManager.getLogger("Framework");

    public Framework()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::onCommonSetup);
        bus.addListener(this::onLoadComplete);
        bus.addListener(this::onRegister);
        bus.addListener(ForgeSyncedEntityDataHandler::registerCapabilities);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            bus.addListener(ClientHandler::registerReloadListener);
        });
        Registration.init();
    }

    private void onCommonSetup(FMLCommonSetupEvent event)
    {
        event.enqueueWork(() -> {
            Bootstrap.init();
        });
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
    }

    private void onLoadComplete(FMLLoadCompleteEvent event)
    {
        GameStates.setLoaded();
    }
}