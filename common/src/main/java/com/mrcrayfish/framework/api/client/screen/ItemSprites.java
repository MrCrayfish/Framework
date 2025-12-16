package com.mrcrayfish.framework.api.client.screen;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableMap;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

@Beta
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

    public ResourceLocation getSprite(boolean enabled, boolean hovered, boolean selected)
    {
        return this.map.get(this.calculateKey(enabled, hovered, selected));
    }

    public static ItemSprites of(@Nullable ResourceLocation all)
    {
        return new ItemSprites(all, all, all, all, all, all, all, all);
    }

    public static ItemSprites of(@Nullable ResourceLocation enabled, @Nullable ResourceLocation disabled, @Nullable ResourceLocation enabledSelected)
    {
        return new ItemSprites(enabled, disabled, enabled, disabled, enabledSelected, disabled, enabledSelected, disabled);
    }

    public static ItemSprites of(@Nullable ResourceLocation enabled, @Nullable ResourceLocation disabled, @Nullable ResourceLocation enabledHovered, @Nullable ResourceLocation disabledHovered, @Nullable ResourceLocation enabledSelected, @Nullable ResourceLocation disabledSelected, @Nullable ResourceLocation enabledHoveredSelected, @Nullable ResourceLocation disabledHoveredSelected)
    {
        return new ItemSprites(enabled, disabled, enabledHovered, disabledHovered, enabledSelected, disabledSelected, enabledHoveredSelected, disabledHoveredSelected);
    }

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

        public ItemSprites build()
        {
            return new ItemSprites(this.enabled, this.disabled, this.enabledHovered, this.disabledHovered, this.enabledSelected, this.disabledSelected, this.enabledHoveredSelected, this.disabledHoveredSelected);
        }

        public Builder setEnabled(@Nullable ResourceLocation enabled)
        {
            this.enabled = enabled;
            return this;
        }

        public Builder setDisabled(@Nullable ResourceLocation disabled)
        {
            this.disabled = disabled;
            return this;
        }

        public Builder setEnabledHovered(@Nullable ResourceLocation enabledHovered)
        {
            this.enabledHovered = enabledHovered;
            return this;
        }

        public Builder setDisabledHovered(@Nullable ResourceLocation disabledHovered)
        {
            this.disabledHovered = disabledHovered;
            return this;
        }

        public Builder setEnabledSelected(@Nullable ResourceLocation enabledSelected)
        {
            this.enabledSelected = enabledSelected;
            return this;
        }

        public Builder setDisabledSelected(@Nullable ResourceLocation disabledSelected)
        {
            this.disabledSelected = disabledSelected;
            return this;
        }

        public Builder setEnabledHoveredSelected(@Nullable ResourceLocation enabledHoveredSelected)
        {
            this.enabledHoveredSelected = enabledHoveredSelected;
            return this;
        }

        public Builder setDisabledHoveredSelected(@Nullable ResourceLocation disabledHoveredSelected)
        {
            this.disabledHoveredSelected = disabledHoveredSelected;
            return this;
        }
    }
}
