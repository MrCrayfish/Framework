package com.mrcrayfish.framework.api.client.screen;

import com.mrcrayfish.framework.api.client.screen.overlay.Window;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.navigation.ScreenRectangle;

import java.util.function.BiConsumer;

/**
 * Defines anchor rules that can be used to position a {@link LayoutElement} relative to a given
 * {@link ScreenRectangle}. For example, a {@link Button} can be placed below and aligned to the left
 * edge of another widget by using {@link #BELOW_LEFT} and calling {@link #apply(LayoutElement, LayoutElement)}
 * with the first parameter being the button, and the second being the widget to place the button
 * relative to.
 *
 * <p>Anchors are primarily used for positioning {@link Window} overlays, but can be implemented
 * or used in other contexts if needed.
 */
public enum Anchor
{
    ABOVE_LEFT((element, rectangle) -> {
        element.setX(rectangle.left());
        element.setY(rectangle.top() - element.getHeight());
    }),
    ABOVE_RIGHT((element, rectangle) -> {
        element.setX(rectangle.right() - element.getWidth());
        element.setY(rectangle.top() - element.getHeight());
    }),
    BELOW_LEFT((element, rectangle) -> {
        element.setX(rectangle.left());
        element.setY(rectangle.bottom());
    }),
    BELOW_RIGHT((element, rectangle) -> {
        element.setX(rectangle.right() - element.getWidth());
        element.setY(rectangle.bottom());
    }),
    END_TOP((element, rectangle) -> {
        element.setX(rectangle.right());
        element.setY(rectangle.top());
    }),
    END_BOTTOM((element, rectangle) -> {
        element.setX(rectangle.right());
        element.setY(rectangle.bottom() - element.getHeight());
    }),
    CENTERED((element, rectangle) -> {
        int x = (rectangle.width() - element.getWidth()) / 2;
        int y = (rectangle.height() - element.getHeight()) / 2;
        element.setPosition(x, y);
    });

    private final BiConsumer<LayoutElement, ScreenRectangle> aligner;

    Anchor(BiConsumer<LayoutElement, ScreenRectangle> positioner)
    {
        this.aligner = positioner;
    }

    /**
     * Positions the given {@link LayoutElement} relative to another {@link LayoutElement} using the
     * logic of this anchor. This method simply calls {@link #apply(LayoutElement, ScreenRectangle)}
     * with the result of {@link LayoutElement#getRectangle()} of the source layout element.
     *
     * @param element the {@link LayoutElement} to position
     * @param source  the {@link LayoutElement} whose bounds define the reference area for alignment
     */
    public void apply(LayoutElement element, LayoutElement source)
    {
        this.apply(element, source.getRectangle());
    }

    /**
     * Positions the given {@link LayoutElement} relative to a {@link ScreenRectangle} using the
     * logic of this anchor.
     *
     * @param element the {@link LayoutElement} to position
     * @param source  the {@link ScreenRectangle} whose bounds define the reference area for alignment
     */
    public void apply(LayoutElement element, ScreenRectangle source)
    {
        this.aligner.accept(element, source);
    }
}
