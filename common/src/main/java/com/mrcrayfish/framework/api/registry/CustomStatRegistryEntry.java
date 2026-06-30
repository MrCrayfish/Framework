package com.mrcrayfish.framework.api.registry;

import com.mrcrayfish.framework.registry.RegisterConsumer;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;
import org.jetbrains.annotations.NotNull;

/**
 * Author: MrCrayfish
 */
public final class CustomStatRegistryEntry extends RegistryEntry<Identifier>
{
    private final StatFormatter formatter;

    CustomStatRegistryEntry(Registry<?> registry, ResourceKey<@NotNull Identifier> key, StatFormatter formatter)
    {
        super(registry, key, key::identifier);
        this.formatter = formatter;
    }

    @Override
    public void register(RegisterConsumer<Identifier> consumer)
    {
        super.register(consumer);
        Stats.CUSTOM.get(this.key.identifier(), this.formatter);
    }
}
