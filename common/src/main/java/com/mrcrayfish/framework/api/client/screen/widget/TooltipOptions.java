package com.mrcrayfish.framework.api.client.screen.widget;

/**
 * Tooltip options for widgets. These options are intended to be applied together using bitwise ops.
 */
@SuppressWarnings("PointlessBitwiseExpression")
public final class TooltipOptions
{
    private TooltipOptions() {}
    
    /**
     * Prevents tooltips from showing when the active state of the widget is false. Widgets, like
     * buttons, still draw the tooltip even though they appear to the user as disabled. This option
     * will simply stop the draw call.
     */
    public static final int DISABLE_TOOLTIP_WHEN_WIDGET_INACTIVE = 1 << 0;

    /**
     * It is very often that tooltips become overloaded with information. Instead of showing the
     * full tooltip, it is shortened until the user pressed the shift key. Since Framework widgets
     * reuse a cached version of the tooltip, this option will flag that cache to be invalidated
     * when the shift key is pressed or released.
     */
    public static final int REBUILD_TOOLTIP_ON_SHIFT = 1 << 1;

    /**
     * Framework widgets, by default, only invalidate the tooltip cache when the widget is clicked.
     * This is an additional option to invalidate and rebuild the tooltip when the cursor initially
     * hovers over the widget.
     */
    public static final int REBUILD_TOOLTIP_ON_WIDGET_HOVER = 1 << 2;
}
