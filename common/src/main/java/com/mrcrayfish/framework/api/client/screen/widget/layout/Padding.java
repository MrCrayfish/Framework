package com.mrcrayfish.framework.api.client.screen.widget.layout;

@SuppressWarnings("ClassCanBeRecord")
public final class Padding
{
    private final int left;
    private final int top;
    private final int right;
    private final int bottom;

    private Padding(int left, int top, int right, int bottom)
    {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    private Padding(int padding)
    {
        this(padding, padding, padding, padding);
    }

    public int left()
    {
        return this.left;
    }

    public int top()
    {
        return this.top;
    }

    public int right()
    {
        return this.right;
    }

    public int bottom()
    {
        return this.bottom;
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
