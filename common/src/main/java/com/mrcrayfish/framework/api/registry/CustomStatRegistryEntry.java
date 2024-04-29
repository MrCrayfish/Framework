package com.mrcrayfish.framework.api.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;

/**
 * Author: MrCrayfish
 */
public final class CustomStatRegistryEntry extends RegistryEntry<ResourceLocation>
{
    private final StatFormatter formatter;

    CustomStatRegistryEntry(Registry<?> registry, ResourceLocation id, StatFormatter formatter)
    {
        super(registry, id, () -> id);
        this.formatter = formatter;
    }

    @Override
    public void register(IRegisterFunction function)
    {
        super.register(function);
        Stats.CUSTOM.get(this.id, this.formatter);
    }
}
