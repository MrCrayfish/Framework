package com.mrcrayfish.framework;

import com.mrcrayfish.framework.api.registry.RegistryEntry;
import com.mrcrayfish.framework.platform.Services;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Author: MrCrayfish
 */
public final class Registration
{
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

    public static Collection<RegistryEntry<?>> getAllRegistryEntries()
    {
        return ENTRY_MAP.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }
}
