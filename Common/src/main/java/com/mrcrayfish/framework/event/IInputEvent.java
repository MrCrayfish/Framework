package com.mrcrayfish.framework.event;

import com.mrcrayfish.framework.api.event.IFrameworkEvent;

/**
 * Author: MrCrayfish
 */
public interface IInputEvent extends IFrameworkEvent
{
    @FunctionalInterface
    interface Key extends IInputEvent
    {
        void handle(int key, int scanCode, int action, int modifiers);
    }
}
