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
        list.add(Registries.ATTRIBUTE.location());
        list.add(Registries.DATA_COMPONENT_TYPE.location());
        list.add(Registries.GAME_EVENT.location());
        list.add(Registries.SOUND_EVENT.location());
        list.add(Registries.FLUID.location());
        list.add(Registries.MOB_EFFECT.location());
        list.add(Registries.BLOCK.location());
        list.add(Registries.ENCHANTMENT.location());
        list.add(Registries.ENTITY_TYPE.location());
        list.add(Registries.ITEM.location());
        list.add(Registries.POTION.location());
        list.add(Registries.PARTICLE_TYPE.location());
        list.add(Registries.BLOCK_ENTITY_TYPE.location());
        list.add(Registries.CUSTOM_STAT.location());
        list.add(Registries.MENU.location());
        list.add(Registries.RECIPE_TYPE.location());
        list.add(Registries.RECIPE_SERIALIZER.location());
        list.add(Registries.COMMAND_ARGUMENT_TYPE.location());
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
