package com.mrcrayfish.framework.api.client.screen.widget.renderer;

import net.minecraft.client.gui.GuiGraphics;

public interface ContentRenderer<T>
{
    void draw(T widget, GuiGraphics graphics, int mouseX, int mouseY, float partialTick);
}
