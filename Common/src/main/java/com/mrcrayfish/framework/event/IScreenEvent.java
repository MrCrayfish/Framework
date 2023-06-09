package com.mrcrayfish.framework.event;

import com.mrcrayfish.framework.api.event.IFrameworkEvent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

import java.util.List;
import java.util.function.Consumer;

/**
 * Author: MrCrayfish
 */
public interface IScreenEvent extends IFrameworkEvent
{
    @FunctionalInterface
    interface AfterDrawContainerBackground extends IScreenEvent
    {
        void handle(AbstractContainerScreen<?> screen, GuiGraphics graphics, int mouseX, int mouseY);
    }

    @FunctionalInterface
    interface Init extends IScreenEvent
    {
        void handle(Screen screen);
    }

    @FunctionalInterface
    interface BeforeDraw extends IScreenEvent
    {
        void handle(Screen screen, GuiGraphics graphics, int mouseX, int mouseY, float partialTick);
    }

    @FunctionalInterface
    interface AfterDraw extends IScreenEvent
    {
        void handle(Screen screen, GuiGraphics graphics, int mouseX, int mouseY, float partialTick);
    }

    @FunctionalInterface
    interface Opened extends IScreenEvent
    {
        void handle(Screen screen);
    }

    interface ModifyWidgets extends IScreenEvent
    {
         void handle(Screen screen, List<AbstractWidget> widgets, Consumer<AbstractWidget> add, Consumer<AbstractWidget> remove);
    }
}
