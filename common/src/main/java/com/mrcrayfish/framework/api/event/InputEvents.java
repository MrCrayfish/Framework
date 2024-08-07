package com.mrcrayfish.framework.api.event;

import com.mrcrayfish.framework.event.IInputEvent;

/**
 * Author: MrCrayfish
 */
public class InputEvents
{
    public static final FrameworkEvent<IInputEvent.RegisterKeyMapping> REGISTER_KEY_MAPPING = new FrameworkEvent<>(listeners -> (consumer) -> {
        listeners.forEach(listener -> listener.handle(consumer));
    });

    public static final FrameworkEvent<IInputEvent.Key> KEY = new FrameworkEvent<>(listeners -> (key, scanCode, action, modifiers) -> {
       listeners.forEach(listener -> listener.handle(key, scanCode, action, modifiers));
    });

    public static final FrameworkEvent<IInputEvent.Click> CLICK = new FrameworkEvent<>(listeners -> (attack, use, pick, hand) -> {
        for(var listener : listeners) {
            if(listener.handle(attack, use, pick, hand)) {
                return true;
            }
        }
        return false;
    });
}
