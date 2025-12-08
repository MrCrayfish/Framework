package com.mrcrayfish.framework.api.client.screen.widget.layout;

public abstract sealed class EdgeInsets permits Border, Padding
{
    private final int left;
    private final int top;
    private final int right;
    private final int bottom;

    protected EdgeInsets(int left, int top, int right, int bottom)
    {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
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
}
