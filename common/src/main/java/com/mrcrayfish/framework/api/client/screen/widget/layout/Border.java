package com.mrcrayfish.framework.api.client.screen.widget.layout;

/**
 * Represents a configurable border with customisable thickness for each side
 */
public final class Border extends EdgeInsets
{
    public static final Border ZERO = Border.of(0);

    private Border(int left, int top, int right, int bottom)
    {
        super(left, top, right, bottom);
    }

    private Border(int border)
    {
        this(border, border, border, border);
    }

    /**
     * Creates a {@link Border} instance with the same border value on all sides.
     *
     * @param all the border for left, top, right, and bottom in pixels
     * @return a new {@link Border} instance
     */
    public static Border of(int all)
    {
        return new Border(all);
    }

    /**
     * Creates a {@link Border} instance with a custom border for each side.
     *
     * @param left   the border for the left side in pixels
     * @param top    the border for the top side in pixels
     * @param right  the border for the right side in pixels
     * @param bottom the border for the bottom side in pixels
     * @return a new {@link Border} instance
     */
    public static Border of(int left, int top, int right, int bottom)
    {
        return new Border(left, top, right, bottom);
    }
}
