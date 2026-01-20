package com.mrcrayfish.framework.api.client.screen.widget.layout;

/**
 * Represents a configurable padding with customisable values for each side.
 */
public final class Padding extends EdgeInsets
{
    public static final Padding ZERO = Padding.of(0);

    private Padding(int left, int top, int right, int bottom)
    {
        super(left, top, right, bottom);
    }

    private Padding(int padding)
    {
        this(padding, padding, padding, padding);
    }

    /**
     * Creates a {@link Padding} instance with the same padding value on all sides.
     *
     * @param all the padding for left, top, right, and bottom in pixels
     * @return a new {@link Padding} instance
     */
    public static Padding of(int all)
    {
        return new Padding(all);
    }

    /**
     * Creates a {@link Padding} instance with separate horizontal and vertical padding values. The
     * horizontal value is applied to left and right, and the vertical value is applied to top and
     * bottom.
     *
     * @param horizontal the padding for left and right in pixels
     * @param vertical   the padding for top and bottom in pixels
     * @return a new {@link Padding} instance
     */
    public static Padding of(int horizontal, int vertical)
    {
        return new Padding(horizontal, vertical, horizontal, vertical);
    }

    /**
     * Creates a {@link Padding} instance with custom padding for each side.
     *
     * @param left   the padding for the left side in pixels
     * @param top    the padding for the top side in pixels
     * @param right  the padding for the right side in pixels
     * @param bottom the padding for the bottom side in pixels
     * @return a new {@link Padding} instance
     */
    public static Padding of(int left, int top, int right, int bottom)
    {
        return new Padding(left, top, right, bottom);
    }
}
