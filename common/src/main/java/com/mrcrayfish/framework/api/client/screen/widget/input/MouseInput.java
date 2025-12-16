package com.mrcrayfish.framework.api.client.screen.widget.input;

import com.google.common.annotations.Beta;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Beta
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

    @Nullable
    public static MouseInput fromButton(int button)
    {
        return BUTTON_TO_INPUT.get(button);
    }

    public int button()
    {
        return this.button;
    }
}
