package com.mrcrayfish.framework.api.client.screen.overlay.impl;

import com.google.common.annotations.Beta;
import com.mrcrayfish.framework.api.client.screen.overlay.Window;
import com.mrcrayfish.framework.api.client.screen.widget.layout.Margin;
import com.mrcrayfish.framework.api.client.screen.widget.layout.Padding;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

@Beta
public final class ContextMenu extends Window
{
    private ContextMenu(@Nullable Function<ContextMenu, Layout> content, Margin outerMargin, Padding contentPadding, ResourceLocation background)
    {
        super(content, outerMargin, contentPadding, background);
    }

    public void show(int x, int y)
    {
        this.show(bounds -> {
            this.setPosition(x, y);
            this.clampToBounds(bounds);
        });
    }

    @Override
    public void renderBackground(GuiGraphics graphics)
    {
        var window = Minecraft.getInstance().getWindow();
        graphics.fill(0, 0, window.getWidth(), window.getHeight(), 0x20000000);
    }
}
