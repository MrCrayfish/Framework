package com.mrcrayfish.framework.event;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.framework.api.event.IFrameworkEvent;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

/**
 * Author: MrCrayfish
 */
public interface IScreenEvent extends IFrameworkEvent
{
    @FunctionalInterface
    interface AfterDrawContainerBackground extends IScreenEvent
    {
        void handle(AbstractContainerScreen<?> screen, PoseStack stack, int mouseX, int mouseY);
    }
}
