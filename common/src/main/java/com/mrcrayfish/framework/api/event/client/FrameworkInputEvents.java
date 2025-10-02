package com.mrcrayfish.framework.api.event.client;

import com.mrcrayfish.framework.api.event.FrameworkEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.player.ClientInput;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

/**
 * Author: MrCrayfish
 */
public final class FrameworkInputEvents
{
    /**
     * @deprecated Use {@link #KEY_PRESS} event instead
     */
    @Deprecated(forRemoval = true, since = "1.21.9")
    public static final FrameworkEvent<Key> KEY = new FrameworkEvent<>(listeners -> (key, scanCode, action, modifiers) -> {
       listeners.forEach(listener -> listener.handle(key, scanCode, action, modifiers));
    });

    public static final FrameworkEvent<KeyPress> KEY_PRESS = new FrameworkEvent<>(listeners -> (action, event) -> {
        listeners.forEach(listener -> listener.handle(action, event));
    });

    public static final FrameworkEvent<Interaction> INTERACTION = new FrameworkEvent<>(listeners -> (attack, use, pick, hand) -> {
        for(var listener : listeners) {
            if(listener.handle(attack, use, pick, hand)) {
                return true;
            }
        }
        return false;
    });

    public static final FrameworkEvent<ClientInputUpdate> CLIENT_INPUT_UPDATE = new FrameworkEvent<>(listeners -> (player, input) -> {
        listeners.forEach(listener -> listener.handle(player, input));
    });

    @FunctionalInterface
    public interface Key
    {
        void handle(int key, int scanCode, int action, int modifiers);
    }

    @FunctionalInterface
    public interface KeyPress
    {
        void handle(int action, KeyEvent event);
    }

    @FunctionalInterface
    public interface Interaction
    {
        boolean handle(boolean attack, boolean use, boolean pick, InteractionHand hand);
    }

    @FunctionalInterface
    public interface ClientInputUpdate
    {
        void handle(Player player, ClientInput input);
    }
}
