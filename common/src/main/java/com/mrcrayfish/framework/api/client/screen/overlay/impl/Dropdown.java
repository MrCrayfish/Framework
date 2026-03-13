package com.mrcrayfish.framework.api.client.screen.overlay.impl;

import com.mrcrayfish.framework.api.client.screen.Anchor;
import com.mrcrayfish.framework.api.client.screen.overlay.Window;
import com.mrcrayfish.framework.api.client.screen.widget.layout.Margin;
import com.mrcrayfish.framework.api.client.screen.widget.layout.Padding;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public final class Dropdown extends Window
{
    private Dropdown(@Nullable Function<Dropdown, Layout> content, Margin outerMargin, Padding contentPadding, Identifier background)
    {
        super(content, outerMargin, contentPadding, background);
    }

    public void show(LayoutElement element, Anchor anchor)
    {
        this.show(bounds -> {
            anchor.apply(this, element);
            this.clampToBounds(bounds);
        });
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder extends Window.Builder<Builder, Dropdown>
    {
        private Builder() {}

        @Override
        public Dropdown build()
        {
            return new Dropdown(this.layout, this.outerMargin, this.contentPadding, this.background);
        }
    }
}
