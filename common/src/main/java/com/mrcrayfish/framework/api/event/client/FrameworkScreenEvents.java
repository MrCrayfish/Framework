package com.mrcrayfish.framework.api.event.client;

import com.mrcrayfish.framework.api.event.FrameworkEvent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

/**
 * Author: MrCrayfish
 */
public final class FrameworkScreenEvents
{
    public static final FrameworkEvent<Init> INIT = new FrameworkEvent<>(listeners -> (screen, widgets, add, remove) -> {
        listeners.forEach(listener -> listener.handle(screen, widgets, add, remove));
    });

    public static final FrameworkEvent<BeforeExtract> BEFORE_EXTRACT = new FrameworkEvent<>(listeners -> ((screen, poseStack, mouseX, mouseY, partialTick) -> {
        listeners.forEach(listener -> listener.handle(screen, poseStack, mouseX, mouseY, partialTick));
    }));

    public static final FrameworkEvent<AfterExtract> AFTER_EXTRACT = new FrameworkEvent<>(listeners -> ((screen, poseStack, mouseX, mouseY, partialTick) -> {
        listeners.forEach(listener -> listener.handle(screen, poseStack, mouseX, mouseY, partialTick));
    }));

    public static final FrameworkEvent<AfterExtractBackground> AFTER_EXTRACT_BACKGROUND = new FrameworkEvent<>(listeners -> (screen, stack, mouseX, mouseY, partialTick) -> {
        listeners.forEach(listener -> listener.handle(screen, stack, mouseX, mouseY, partialTick));
    });

    public static final FrameworkEvent<Opened> OPENED = new FrameworkEvent<>(listeners -> (screen) -> {
        listeners.forEach(listener -> listener.handle(screen));
    });

    public static final FrameworkEvent<Closed> CLOSED = new FrameworkEvent<>(listeners -> (screen) -> {
        listeners.forEach(listener -> listener.handle(screen));
    });

    @FunctionalInterface
    public interface Init
    {
        void handle(Screen screen, List<AbstractWidget> widgets, Consumer<AbstractWidget> add, Consumer<AbstractWidget> remove);
    }

    @FunctionalInterface
    public interface BeforeExtract
    {
        void handle(Screen screen, GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick);
    }

    @FunctionalInterface
    public interface AfterExtract
    {
        void handle(Screen screen, GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick);
    }

    @FunctionalInterface
    public interface AfterExtractBackground
    {
        void handle(Screen screen, GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick);
    }

    @FunctionalInterface
    public interface Opened
    {
        void handle(@Nullable Screen screen);
    }

    @FunctionalInterface
    public interface Closed
    {
        void handle(Screen screen);
    }
}
