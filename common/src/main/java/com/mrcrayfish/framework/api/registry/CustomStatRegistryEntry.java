package com.mrcrayfish.framework.api.registry;

import com.mrcrayfish.framework.registry.RegisterConsumer;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;

/**
 * Author: MrCrayfish
 */
public final class CustomStatRegistryEntry extends RegistryEntry<Identifier>
{
    private final StatFormatter formatter;

    CustomStatRegistryEntry(Registry<?> registry, Identifier id, StatFormatter formatter)
    {
        super(registry, id, () -> id);
        this.formatter = formatter;
    }

    @Override
    public void register(RegisterConsumer<Identifier> consumer)
    {
        super.register(consumer);
        Stats.CUSTOM.get(this.valueId, this.formatter);
    }
}
