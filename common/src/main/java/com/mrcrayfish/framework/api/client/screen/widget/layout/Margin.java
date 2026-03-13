package com.mrcrayfish.framework.api.client.screen.widget.layout;

/**
 * Represents a configurable margin with customisable values for each side.
 */
public final class Margin extends EdgeInsets
{
    public static final Margin ZERO = Margin.of(0);

    private Margin(int left, int top, int right, int bottom)
    {
        super(left, top, right, bottom);
    }

    private Margin(int margin)
    {
        this(margin, margin, margin, margin);
    }

    /**
     * Creates a {@link Margin} instance with the same margin value on all sides.
     *
     * @param all the margin for left, top, right, and bottom in pixels
     * @return a new {@link Margin} instance
     */
    public static Margin of(int all)
    {
        return new Margin(all);
    }

    /**
     * Creates a {@link Margin} instance with separate horizontal and vertical margin values. The
     * horizontal value is applied to left and right, and the vertical value is applied to top and
     * bottom.
     *
     * @param horizontal the margin for left and right in pixels
     * @param vertical   the margin for top and bottom in pixels
     * @return a new {@link Margin} instance
     */
    public static Margin of(int horizontal, int vertical)
    {
        return new Margin(horizontal, vertical, horizontal, vertical);
    }

    /**
     * Creates a {@link Margin} instance with custom margin for each side.
     *
     * @param left   the margin for the left side in pixels
     * @param top    the margin for the top side in pixels
     * @param right  the margin for the right side in pixels
     * @param bottom the margin for the bottom side in pixels
     * @return a new {@link Margin} instance
     */
    public static Margin of(int left, int top, int right, int bottom)
    {
        return new Margin(left, top, right, bottom);
    }
}
