package com.mrcrayfish.framework.api.client.screen.widget.renderer;

import com.google.common.annotations.Beta;
import net.minecraft.client.gui.GuiGraphics;

@Beta
public interface ContentRenderer<T>
{
    void draw(T widget, GuiGraphics graphics, int mouseX, int mouseY, float partialTick);
}
