package com.mrcrayfish.framework;

import com.mrcrayfish.framework.api.registry.RegistryEntry;
import com.mrcrayfish.framework.platform.Services;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Author: MrCrayfish
 */
public final class Registration
{
    private static final List<ResourceLocation> REGISTRATION_PRIORITY = Util.make(new LinkedList<>(), list -> {
        list.add(Registry.ATTRIBUTE_REGISTRY.location());
        list.add(Registry.GAME_EVENT_REGISTRY.location());
        list.add(Registry.SOUND_EVENT_REGISTRY.location());
        list.add(Registry.FLUID_REGISTRY.location());
        list.add(Registry.MOB_EFFECT_REGISTRY.location());
        list.add(Registry.BLOCK_REGISTRY.location());
        list.add(Registry.ENCHANTMENT_REGISTRY.location());
        list.add(Registry.ENTITY_TYPE_REGISTRY.location());
        list.add(Registry.ITEM_REGISTRY.location());
        list.add(Registry.POTION_REGISTRY.location());
        list.add(Registry.PARTICLE_TYPE_REGISTRY.location());
        list.add(Registry.BLOCK_ENTITY_TYPE_REGISTRY.location());
        list.add(Registry.CUSTOM_STAT_REGISTRY.location());
        list.add(Registry.MENU_REGISTRY.location());
        list.add(Registry.RECIPE_TYPE_REGISTRY.location());
        list.add(Registry.RECIPE_SERIALIZER_REGISTRY.location());
        list.add(Registry.COMMAND_ARGUMENT_TYPE_REGISTRY.location());
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
                .sorted(Comparator.comparing(entry -> {
                    int index = REGISTRATION_PRIORITY.indexOf(entry.getRegistry().key().location());
                    return index != -1 ? index : 1000;
                }))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
