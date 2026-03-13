package com.mrcrayfish.framework.api.client.screen.overlay.impl;

import com.google.common.annotations.Beta;
import com.mrcrayfish.framework.api.client.screen.overlay.Window;
import com.mrcrayfish.framework.api.client.screen.widget.layout.Margin;
import com.mrcrayfish.framework.api.client.screen.widget.layout.Padding;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

@Beta
public final class Modal extends Window
{
    private Modal(@Nullable Function<Modal, Layout> layout, Margin outerMargin, Padding contentPadding, @Nullable ResourceLocation background)
    {
        super(layout, outerMargin, contentPadding, background);
    }

    public void show(Position position)
    {
        this.show(bounds -> {
            int x = (bounds.width() - this.getWidth()) / 2;
            int y = (int) ((bounds.height() - this.getHeight()) * position.offset);
            this.setPosition(x, y);
            this.clampToBounds(bounds);
        });
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder extends Window.Builder<Builder, Modal>
    {
        private Builder() {}

        @Override
        public Modal build()
        {
            return new Modal(this.layout, this.outerMargin, this.contentPadding, this.background);
        }
    }

    public enum Position
    {
        TOP(0.0F),
        CENTER(0.5F),
        BOTTOM(1.0F);

        final float offset;

        Position(float offset)
        {
            this.offset = offset;
        }
    }
}
