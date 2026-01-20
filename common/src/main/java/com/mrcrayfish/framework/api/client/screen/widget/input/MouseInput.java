package com.mrcrayfish.framework.api.client.screen.widget.input;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents mouse input actions for left, middle, or right click, mapped to GLFW button constants.
 */
public enum MouseInput
{
    LEFT_CLICK(GLFW.GLFW_MOUSE_BUTTON_LEFT),
    MIDDLE_CLICK(GLFW.GLFW_MOUSE_BUTTON_MIDDLE),
    RIGHT_CLICK(GLFW.GLFW_MOUSE_BUTTON_RIGHT);

    private static final Map<Integer, MouseInput> BUTTON_TO_INPUT = Stream.of(MouseInput.values()).collect(Collectors.toUnmodifiableMap(MouseInput::button, Function.identity()));

    private final int button;

    MouseInput(int button)
    {
        this.button = button;
    }

    /**
     * Resolves a {@link MouseInput} from a raw GLFW mouse button value.
     *
     * @param button the GLFW mouse button constant
     * @return the corresponding {@link MouseInput}, or null if the button is not supported
     */
    @Nullable
    public static MouseInput fromButton(int button)
    {
        return BUTTON_TO_INPUT.get(button);
    }

    /**
     * @return the GLFW mouse button constant associated with this input.
     */
    public int button()
    {
        return this.button;
    }
}
