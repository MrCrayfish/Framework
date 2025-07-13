package com.mrcrayfish.framework;

import com.mrcrayfish.framework.api.registry.BlockRegistryEntry;
import com.mrcrayfish.framework.api.registry.IRegisterFunction;
import com.mrcrayfish.framework.client.ClientFrameworkForge;
import com.mrcrayfish.framework.entity.sync.ForgeSyncedEntityDataHandler;
import com.mrcrayfish.framework.event.ForgeEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
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
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Consumer;
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

        // Don't ask...
        bus.addGenericListener(Attribute.class, (RegistryEvent.Register<Attribute> event) -> this.onRegister(event));
        bus.addGenericListener(Block.class, (RegistryEvent.Register<Block> event) -> this.onRegister(event));
        bus.addGenericListener(BlockEntityType.class, (RegistryEvent.Register<BlockEntityType<?>> event) -> this.onRegister(event));
        bus.addGenericListener(Enchantment.class, (RegistryEvent.Register<Enchantment> event) -> this.onRegister(event));
        bus.addGenericListener(EntityType.class, (RegistryEvent.Register<EntityType<?>> event) -> this.onRegister(event));
        bus.addGenericListener(Fluid.class, (RegistryEvent.Register<Fluid> event) -> this.onRegister(event));
        bus.addGenericListener(Item.class, (RegistryEvent.Register<Item> event) -> this.onRegister(event));
        bus.addGenericListener(MenuType.class, (RegistryEvent.Register<MenuType<?>> event) -> this.onRegister(event));
        bus.addGenericListener(MobEffect.class, (RegistryEvent.Register<MobEffect> event) -> this.onRegister(event));
        bus.addGenericListener(ParticleType.class, (RegistryEvent.Register<ParticleType<?>> event) -> this.onRegister(event));
        bus.addGenericListener(Potion.class, (RegistryEvent.Register<Potion> event) -> this.onRegister(event));
        bus.addGenericListener(RecipeSerializer.class, (RegistryEvent.Register<RecipeSerializer<?>> event) -> this.onRegister(event));
        bus.addGenericListener(SoundEvent.class, (RegistryEvent.Register<SoundEvent> event) -> this.onRegister(event));

        bus.addListener(ForgeSyncedEntityDataHandler::registerCapabilities);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            bus.addListener(this::onClientSetup);
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

        // Need to call this manually
        event.enqueueWork(() -> {
            Registration.get(Registry.CUSTOM_STAT_REGISTRY).forEach(entry -> {
                entry.register(new IRegisterFunction() {
                    @Override
                    public <T> void call(Registry<T> registry, ResourceLocation name, Supplier<T> valueSupplier) {
                        Registry.register(registry, name, valueSupplier.get());
                    }
                });
            });
        });
    }

    private void onClientSetup(FMLClientSetupEvent event)
    {
        event.enqueueWork(ClientFrameworkForge::init);
    }

    @SuppressWarnings("unchecked")
    private <T extends IForgeRegistryEntry<T>> void onRegister(RegistryEvent.Register<T> event)
    {
        Registration.get(event.getRegistry().getRegistryKey()).forEach(entry -> {
            entry.register(new IRegisterFunction() {
                @Override
                public <R> void call(Registry<R> registry, ResourceLocation name, Supplier<R> valueSupplier) {
                    R value = valueSupplier.get();
                    ((IForgeRegistryEntry<R>) value).setRegistryName(name);
                    event.getRegistry().register((T) value);
                }
            });
        });

        // Special case for block registry entries to register items
        if(event.getRegistry().getRegistryKey().equals(Registry.ITEM_REGISTRY))
        {
            Registration.get(Registry.BLOCK_REGISTRY).forEach(entry -> {
                if(entry instanceof BlockRegistryEntry<?, ?> blockEntry) {
                    blockEntry.item().ifPresent(item -> {
                        ((IForgeRegistryEntry<?>) item).setRegistryName(entry.getId());
                        event.getRegistry().register((T) item);
                    });
                }
            });
        }
    }

    private void onLoadComplete(FMLLoadCompleteEvent event)
    {
        FrameworkData.setLoaded();
    }
}
