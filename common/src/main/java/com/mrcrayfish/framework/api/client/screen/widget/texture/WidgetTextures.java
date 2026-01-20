package com.mrcrayfish.framework.api.client.screen.widget.texture;

import com.google.common.collect.ImmutableMap;

public final class WidgetTextures
{
    private final ImmutableMap<Integer, FrameworkTexture> map;

    public WidgetTextures(FrameworkTexture enabled, FrameworkTexture disabled, FrameworkTexture enabledHovered, FrameworkTexture disabledHovered)
    {
        ImmutableMap.Builder<Integer, FrameworkTexture> builder = ImmutableMap.builder();
        builder.put(this.calculateKey(true, false), enabled);
        builder.put(this.calculateKey(false, false), disabled);
        builder.put(this.calculateKey(true, true), enabledHovered);
        builder.put(this.calculateKey(false, true), disabledHovered);
        this.map = builder.build();
    }

    public WidgetTextures(FrameworkTexture enabled, FrameworkTexture disabled, FrameworkTexture hovered)
    {
        this(enabled, disabled, hovered, disabled);
    }

    public WidgetTextures(FrameworkTexture enabled, FrameworkTexture hovered)
    {
        this(enabled, enabled, hovered, hovered);
    }

    private int calculateKey(boolean enabled, boolean hovered)
    {
        int key = 0;
        if(enabled) key |= 1;
        if(hovered) key |= 1 << 1;
        return key;
    }

    /**
     * Retrieves the texture resource location based on the provided state parameters.
     *
     * @param enabled  indicates the enabled state
     * @param hovered  indicates the hovered state
     * @return a {@link FrameworkTexture} pointing to a texture based on the given states, or null
     */
    public FrameworkTexture get(boolean enabled, boolean hovered)
    {
        return this.map.get(this.calculateKey(enabled, hovered));
    }
}
