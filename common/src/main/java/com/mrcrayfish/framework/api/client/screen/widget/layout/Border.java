package com.mrcrayfish.framework.api.client.screen.widget.layout;

public final class Border extends EdgeInsets
{
    private Border(int left, int top, int right, int bottom)
    {
        super(left, top, right, bottom);
    }

    private Border(int padding)
    {
        this(padding, padding, padding, padding);
    }

    public static Border of(int all)
    {
        return new Border(all);
    }

    public static Border of(int left, int top, int right, int bottom)
    {
        return new Border(left, top, right, bottom);
    }
}
