package com.mrcrayfish.framework;

import com.mrcrayfish.framework.api.registry.RegistryEntry;
import com.mrcrayfish.framework.platform.Services;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Author: MrCrayfish
 */
public final class Registration
{
    private static final Map<ResourceLocation, Integer> REGISTRATION_PRIORITY = Util.make(new HashMap<>(), map -> {
        map.put(Registries.BLOCK.location(), 0);
        map.put(Registries.ITEM.location(), 1);
        map.put(Registries.FLUID.location(), 2);
        map.put(Registries.MOB_EFFECT.location(), 3);
        map.put(Registries.SOUND_EVENT.location(), 4);
        map.put(Registries.POTION.location(), 5);
        map.put(Registries.ENCHANTMENT.location(), 6);
        map.put(Registries.ENTITY_TYPE.location(), 7);
        map.put(Registries.BLOCK_ENTITY_TYPE.location(), 8);
        map.put(Registries.PARTICLE_TYPE.location(), 9);
        map.put(Registries.MENU.location(), 10);
        map.put(Registries.RECIPE_TYPE.location(), 11);
        map.put(Registries.RECIPE_SERIALIZER.location(), 12);
        map.put(Registries.ATTRIBUTE.location(), 13);
        map.put(Registries.COMMAND_ARGUMENT_TYPE.location(), 14);
    });

    private static final Map<ResourceLocation, List<RegistryEntry<?>>> ENTRY_MAP = new HashMap<>();

    public static void init()
    {
        Services.REGISTRATION.getAllRegistryEntries().forEach(entry -> {
            ENTRY_MAP.computeIfAbsent(entry.getRegistry().key().location(), location -> new ArrayList<>()).add(entry);
        });
    }

    public static List<RegistryEntry<?>> get(ResourceKey<? extends Registry<?>> key)
    {
        return ENTRY_MAP.getOrDefault(key.location(), Collections.emptyList());
    }

    public static List<RegistryEntry<?>> getAllRegistryEntries()
    {
        return ENTRY_MAP.values().stream()
                .flatMap(Collection::stream)
                .sorted(Comparator.comparing(entry -> REGISTRATION_PRIORITY.getOrDefault(entry.getRegistry().key().location(), 1000)))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
