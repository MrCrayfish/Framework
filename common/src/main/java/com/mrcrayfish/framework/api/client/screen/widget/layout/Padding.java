package com.mrcrayfish.framework.api.client.screen.widget.layout;

public record Padding(int left, int top, int right, int bottom)
{
    public Padding(int padding)
    {
        this(padding, padding, padding, padding);
    }
}
