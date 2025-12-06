package com.mrcrayfish.framework.api.client.screen;

import com.google.common.collect.ImmutableMap;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public final class ItemSprites
{
    private final ImmutableMap<Integer, ResourceLocation> map;

    public ItemSprites(
            @Nullable ResourceLocation enabled,
            @Nullable ResourceLocation disabled,
            @Nullable ResourceLocation enabledHovered,
            @Nullable ResourceLocation disabledHovered,
            @Nullable ResourceLocation enabledSelected,
            @Nullable ResourceLocation disabledSelected,
            @Nullable ResourceLocation enabledHoveredSelected,
            @Nullable ResourceLocation disabledHoveredSelected)
    {
        ImmutableMap.Builder<Integer, ResourceLocation> builder = ImmutableMap.builder();
        if(disabled != null) builder.put(this.calculateKey(false, false, false), disabled);
        if(enabled != null) builder.put(this.calculateKey(true, false, false), enabled);
        if(disabledHovered != null) builder.put(this.calculateKey(false, true, false), disabledHovered);
        if(enabledHovered != null) builder.put(this.calculateKey(true, true, false), enabledHovered);
        if(disabledSelected != null) builder.put(this.calculateKey(false, false, true), disabledSelected);
        if(enabledSelected != null) builder.put(this.calculateKey(true, false, true), enabledSelected);
        if(disabledHoveredSelected != null) builder.put(this.calculateKey(false, true, true), disabledHoveredSelected);
        if(enabledHoveredSelected != null) builder.put(this.calculateKey(true, true, true), enabledHoveredSelected);
        this.map = builder.build();
    }

    public ItemSprites(ResourceLocation sprite)
    {
        this(sprite, sprite, sprite, sprite, sprite, sprite, sprite, sprite);
    }

    public ItemSprites(ResourceLocation enabled, ResourceLocation disabled, ResourceLocation enabledSelected)
    {
        this(enabled, disabled, enabled, disabled, enabledSelected, disabled, enabledSelected, disabled);
    }

    private int calculateKey(boolean enabled, boolean hovered, boolean selected)
    {
        int key = 0;
        if(enabled) key |= 1;
        if(hovered) key |= 1 << 1;
        if(selected) key |= 1 << 2;
        return key;
    }

    public ResourceLocation getSprite(boolean enabled, boolean hovered, boolean selected)
    {
        return this.map.get(this.calculateKey(enabled, hovered, selected));
    }
}
