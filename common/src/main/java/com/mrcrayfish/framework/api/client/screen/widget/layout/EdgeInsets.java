package com.mrcrayfish.framework.api.client.screen.widget.layout;

/**
 * Represents a base class for defining spacing or thickness for each edge of a rectangular area.
 * Only {@link Border} and {@link Padding} are permitted classes.
 */
public abstract sealed class EdgeInsets permits Border, Padding
{
    private final int left;
    private final int top;
    private final int right;
    private final int bottom;

    /**
     * Constructs an {@link EdgeInsets} instance with the specified values for each side.
     *
     * @param left   the distance from the left edge in pixels
     * @param top    the distance from the top edge in pixels
     * @param right  the distance from the right edge in pixels
     * @param bottom the distance from the bottom edge in pixels
     */
    protected EdgeInsets(int left, int top, int right, int bottom)
    {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    /**
     * @return the left distance in pixels
     */
    public int left()
    {
        return this.left;
    }

    /**
     * @return the top distance in pixels
     */
    public int top()
    {
        return this.top;
    }

    /**
     * @return the right distance in pixels
     */
    public int right()
    {
        return this.right;
    }

    /**
     * @return the bottom distance in pixels
     */
    public int bottom()
    {
        return this.bottom;
    }
}
