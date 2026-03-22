package com.mrcrayfish.framework.api.client.screen.overlay.impl;

import com.google.common.annotations.Beta;
import com.mrcrayfish.framework.api.client.screen.overlay.Window;
import com.mrcrayfish.framework.api.client.screen.widget.layout.Margin;
import com.mrcrayfish.framework.api.client.screen.widget.layout.Padding;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

@Beta
public final class ContextMenu extends Window
{
    private ContextMenu(@Nullable Function<ContextMenu, Layout> content, Margin outerMargin, Padding contentPadding, Identifier background)
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
    public void renderBackground(GuiGraphicsExtractor extractor)
    {
        var window = Minecraft.getInstance().getWindow();
        extractor.fill(0, 0, window.getWidth(), window.getHeight(), 0x20000000);
    }
}
