package com.mrcrayfish.framework.api.client.screen;

import com.google.common.collect.ImmutableMap;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public final class ItemSprites
{
    private final ImmutableMap<Integer, ResourceLocation> map;

    private ItemSprites(
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

    private int calculateKey(boolean enabled, boolean hovered, boolean selected)
    {
        int key = 0;
        if(enabled) key |= 1;
        if(hovered) key |= 1 << 1;
        if(selected) key |= 1 << 2;
        return key;
    }

    /**
     * Retrieves the sprite texture resource location based on the provided state parameters.
     *
     * @param enabled  indicates the enabled state
     * @param hovered  indicates the hovered state
     * @param selected indicates the selected state
     * @return a {@link ResourceLocation} pointing to a texture based on the given states, or null
     */
    @Nullable
    public ResourceLocation getSprite(boolean enabled, boolean hovered, boolean selected)
    {
        return this.map.get(this.calculateKey(enabled, hovered, selected));
    }

    /**
     * Creates a new {@link ItemSprites} where all sprite states point to the same texture location.
     * Alternatively you can use the {@link Builder} to configure the sprites individually.
     *
     * @param all the resource location to be used for all sprite states; can be null
     * @return a new instance of {@link ItemSprites} with the same resource location for all states
     */
    public static ItemSprites of(@Nullable ResourceLocation all)
    {
        return new ItemSprites(all, all, all, all, all, all, all, all);
    }

    /**
     * Creates a new {@link ItemSprites} that matches the states of a vanilla button (enabled, disabled
     * and selected (focused)). Alternatively you can use the {@link Builder} to configure the sprites
     * individually.
     *
     * @param enabled         the resource location to a texture for the enabled state, or null for no texture
     * @param disabled        the resource location to a texture for the disabled state, or null for no texture
     * @param enabledSelected the resource location to a texture for the enabled and selected state, or null for no texture
     * @return a new {@link ItemSprites}  instance initialized with the provided resource locations
     */
    public static ItemSprites of(@Nullable ResourceLocation enabled, @Nullable ResourceLocation disabled, @Nullable ResourceLocation enabledSelected)
    {
        return new ItemSprites(enabled, disabled, enabled, disabled, enabledSelected, disabled, enabledSelected, disabled);
    }

    /**
     * @return A new {@link Builder} to configure and build an {@link ItemSprites} instance
     */
    public static Builder builder()
    {
        return new Builder();
    }

    public static class Builder
    {
        private @Nullable ResourceLocation enabled;
        private @Nullable ResourceLocation disabled;
        private @Nullable ResourceLocation enabledHovered;
        private @Nullable ResourceLocation disabledHovered;
        private @Nullable ResourceLocation enabledSelected;
        private @Nullable ResourceLocation disabledSelected;
        private @Nullable ResourceLocation enabledHoveredSelected;
        private @Nullable ResourceLocation disabledHoveredSelected;

        /**
         * Builds an {@link ItemSprites} instance with the configured sprites
         *
         * @return a new {@link ItemSprites} instance
         */
        public ItemSprites build()
        {
            return new ItemSprites(this.enabled, this.disabled, this.enabledHovered, this.disabledHovered, this.enabledSelected, this.disabledSelected, this.enabledHoveredSelected, this.disabledHoveredSelected);
        }

        /**
         * Sets the sprite for the enabled state
         *
         * @param texture the resource location to a sprite texture, or null
         * @return this {@link Builder} for method chaining
         */
        public Builder setEnabled(@Nullable ResourceLocation texture)
        {
            this.enabled = texture;
            return this;
        }

        /**
         * Sets the sprite for the disabled state.
         *
         * @param texture the resource location to a sprite texture, or null
         * @return this {@link Builder} for method chaining
         */
        public Builder setDisabled(@Nullable ResourceLocation texture)
        {
            this.disabled = texture;
            return this;
        }

        /**
         * Sets the sprite for the enabled and hovered state
         *
         * @param texture the resource location to a sprite texture, or null
         * @return this {@link Builder} for method chaining
         */
        public Builder setEnabledHovered(@Nullable ResourceLocation texture)
        {
            this.enabledHovered = texture;
            return this;
        }

        /**
         * Sets the sprite for the disabled and hovered state
         *
         * @param texture the resource location to a sprite texture, or null
         * @return this {@link Builder} for method chaining
         */
        public Builder setDisabledHovered(@Nullable ResourceLocation texture)
        {
            this.disabledHovered = texture;
            return this;
        }

        /**
         * Sets the sprite for the enabled and selected state
         *
         * @param texture the resource location to a sprite texture, or null
         * @return this {@link Builder} for method chaining
         */
        public Builder setEnabledSelected(@Nullable ResourceLocation texture)
        {
            this.enabledSelected = texture;
            return this;
        }

        /**
         * Sets the sprite for the disabled and selected state
         *
         * @param texture the resource location to a sprite texture, or null
         * @return this {@link Builder} for method chaining
         */
        public Builder setDisabledSelected(@Nullable ResourceLocation texture)
        {
            this.disabledSelected = texture;
            return this;
        }

        /**
         * Sets the sprite for the enabled, hovered, and selected state
         *
         * @param texture the resource location to a sprite texture, or null
         * @return this {@link Builder} for method chaining
         */
        public Builder setEnabledHoveredSelected(@Nullable ResourceLocation texture)
        {
            this.enabledHoveredSelected = texture;
            return this;
        }

        /**
         * Sets the sprite for the disabled, hovered, and selected state
         *
         * @param texture the resource location to a sprite texture, or null
         * @return this {@link Builder} for method chaining
         */
        public Builder setDisabledHoveredSelected(@Nullable ResourceLocation texture)
        {
            this.disabledHoveredSelected = texture;
            return this;
        }
    }
}
