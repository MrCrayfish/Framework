package com.mrcrayfish.framework.api.client.screen.widget.layout;

import com.google.common.annotations.Beta;

@Beta
public final class Padding extends EdgeInsets
{
    private Padding(int left, int top, int right, int bottom)
    {
        super(left, top, right, bottom);
    }

    private Padding(int padding)
    {
        this(padding, padding, padding, padding);
    }

    public static Padding of(int all)
    {
        return new Padding(all);
    }

    public static Padding of(int horizontal, int vertical)
    {
        return new Padding(horizontal, vertical, horizontal, vertical);
    }

    public static Padding of(int left, int top, int right, int bottom)
    {
        return new Padding(left, top, right, bottom);
    }
}
