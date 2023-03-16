package com.mrcrayfish.framework;

import com.mrcrayfish.framework.api.event.FrameworkEvent;
import com.mrcrayfish.framework.api.registry.IRegisterFunction;
import com.mrcrayfish.framework.client.ClientHandler;
import com.mrcrayfish.framework.common.data.SyncedEntityData;
import com.mrcrayfish.framework.network.ForgeNetwork;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoader;
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

    private static boolean gameLoaded = false;

    public Framework()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::onCommonSetup);
        bus.addListener(this::onLoadComplete);
        bus.addListener(this::onRegister);
        bus.addListener(SyncedEntityData::registerCapability);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            bus.addListener(ClientHandler::registerReloadListener);
        });
        MinecraftForge.EVENT_BUS.register(SyncedEntityData.instance());
        Registration.init();
    }

    private void onCommonSetup(FMLCommonSetupEvent event)
    {
        event.enqueueWork(() -> {
            Bootstrap.init();
            ForgeNetwork.init();
            ModLoader.get().postEvent(new FrameworkEvent.Register());
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
        gameLoaded = true;
    }

    public static boolean isGameLoaded()
    {
        return gameLoaded;
    }
}
