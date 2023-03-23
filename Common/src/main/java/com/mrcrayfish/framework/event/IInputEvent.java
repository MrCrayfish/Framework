package com.mrcrayfish.framework.event;

import com.mrcrayfish.framework.api.event.IFrameworkEvent;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.InteractionHand;

import java.util.function.Consumer;

/**
 * Author: MrCrayfish
 */
public interface IInputEvent extends IFrameworkEvent
{
    @FunctionalInterface
    interface RegisterKeyMapping extends IInputEvent
    {
        void handle(Consumer<KeyMapping> consumer);
    }

    @FunctionalInterface
    interface Key extends IInputEvent
    {
        void handle(int key, int scanCode, int action, int modifiers);
    }

    @FunctionalInterface
    interface Click extends IInputEvent
    {
        boolean handle(int button, InteractionHand hand);
    }
}
