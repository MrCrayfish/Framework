package com.mrcrayfish.framework;

import com.mrcrayfish.framework.api.Environment;
import com.mrcrayfish.framework.api.network.FrameworkNetwork;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
import com.mrcrayfish.framework.api.sync.SyncedDataKey;
import com.mrcrayfish.framework.api.util.TaskRunner;
import com.mrcrayfish.framework.client.ClientRegistration;
import com.mrcrayfish.framework.entity.sync.SyncedEntityData;
import com.mrcrayfish.framework.platform.Services;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Author: MrCrayfish
 */
public final class Registration
{
    private static final List<Identifier> REGISTRATION_PRIORITY = Util.make(new LinkedList<>(), list -> {
        list.add(Registries.ATTRIBUTE.identifier());
        list.add(Registries.DATA_COMPONENT_TYPE.identifier());
        list.add(Registries.GAME_EVENT.identifier());
        list.add(Registries.SOUND_EVENT.identifier());
        list.add(Registries.FLUID.identifier());
        list.add(Registries.MOB_EFFECT.identifier());
        list.add(Registries.BLOCK.identifier());
        list.add(Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE.identifier());
        list.add(Registries.ENTITY_TYPE.identifier());
        list.add(Registries.ITEM.identifier());
        list.add(Registries.POTION.identifier());
        list.add(Registries.PARTICLE_TYPE.identifier());
        list.add(Registries.BLOCK_ENTITY_TYPE.identifier());
        list.add(Registries.CUSTOM_STAT.identifier());
        list.add(Registries.MENU.identifier());
        list.add(Registries.RECIPE_TYPE.identifier());
        list.add(Registries.RECIPE_SERIALIZER.identifier());
        list.add(Registries.COMMAND_ARGUMENT_TYPE.identifier());
        list.add(Registries.LOOT_FUNCTION_TYPE.identifier());
    });

    private static final Map<Identifier, List<RegistryEntry<?>>> REGISTRY_ENTRIES = new HashMap<>();
    private static final Set<FrameworkNetwork> NETWORKS = new HashSet<>();

    public static void init()
    {
        Services.REGISTRATION.getRegistryObjects(RegistryEntry.class).forEach(entry -> {
            REGISTRY_ENTRIES.computeIfAbsent(entry.getRegistryKey().identifier(), location -> new ArrayList<>()).add(entry);
        });
        Services.REGISTRATION.getRegistryObjects(SyncedDataKey.class).forEach(key -> {
            SyncedEntityData.instance().registerDataKey((SyncedDataKey<?, ?>) key);
        });
        Services.REGISTRATION.getRegistryObjects(FrameworkNetwork.class).forEach(network -> {
            if(!NETWORKS.add(network)) {
                throw new IllegalStateException("Duplicate network: " + network.id());
            } else {
                fireRegisteredEvent(network);
            }
        });
        TaskRunner.runIf(Environment.CLIENT, () -> ClientRegistration::init);
        Services.REGISTRATION.init();
    }

    public static List<RegistryEntry<?>> get(ResourceKey<? extends Registry<?>> key)
    {
        return REGISTRY_ENTRIES.getOrDefault(key.identifier(), Collections.emptyList());
    }

    public static List<RegistryEntry<?>> getSortedRegistryEntries()
    {
        return REGISTRY_ENTRIES.values().stream()
            .flatMap(Collection::stream)
            .sorted(Comparator.comparing(entry -> {
                int index = REGISTRATION_PRIORITY.indexOf(entry.getRegistryKey().identifier());
                return index != -1 ? index : 1000;
            }))
            .collect(Collectors.toCollection(ArrayList::new));
    }

    public static Set<FrameworkNetwork> getNetworks()
    {
        return Collections.unmodifiableSet(NETWORKS);
    }

    private static void fireRegisteredEvent(Object obj)
    {
        if(obj instanceof Event event)
        {
            event.onRegistered();
        }
    }

    public interface Event
    {
        void onRegistered();
    }
}
