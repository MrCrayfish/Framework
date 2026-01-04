package com.mrcrayfish.framework.api.client.screen.widget;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mrcrayfish.framework.api.client.screen.widget.layout.Border;
import com.mrcrayfish.framework.api.client.screen.widget.layout.Padding;
import com.mrcrayfish.framework.client.ClientUtils;
import com.mrcrayfish.framework.platform.ClientServices;
import com.mrcrayfish.framework.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * An improved version of selection lists, with a focus on a pixel-perfect layout, customisation,
 * and a more open API. One key difference of this implementation is that row width is determined
 * by the available width of the list, rather than a fixed value, which is more akin to lists in
 * a traditional user interface. The implementation also takes advantage of the sprite texture
 * system and can be used to change the texture of the list background, items, scrollbar, and scroller.
 * <p>
 * To get started, use {@link FrameworkSelectionList#builder()} to start constructing a new instance
 * or extend {@link FrameworkSelectionList} to create a custom subclass for full control.
 */
public class FrameworkSelectionList extends ObjectSelectionList<FrameworkSelectionList.Item>
{
    // Defaults for making the list replicate the visual style of vanilla's selection lists
    public static final ResourceLocation DEFAULT_BACKGROUND = Utils.rl("widget/selection_list/background");
    public static final ItemSprites DEFAULT_ITEM_SPRITE = ItemSprites.builder()
        .setEnabledSelected(Utils.rl("widget/selection_list/item_enabled_selected"))
        .setEnabledHoveredSelected(Utils.rl("widget/selection_list/item_enabled_selected"))
        .build();
    public static final Padding DEFAULT_LIST_PADDING = Padding.of(4);
    public static final Border DEFAULT_LIST_BORDER = Border.of(0);
    public static final ScrollerSprites DEFAULT_SCROLLER_SPRITE = ScrollerSprites.of(ResourceLocation.withDefaultNamespace("widget/scroller"));
    public static final ResourceLocation DEFAULT_SCROLL_BAR_BACKGROUND = ResourceLocation.withDefaultNamespace("widget/scroller_background");
    public static final Padding DEFAULT_SCROLL_BAR_PADDING = Padding.of(0);
    public static final Border DEFAULT_SCROLL_BAR_BORDER = Border.of(0);
    public static final Padding DEFAULT_SCROLL_BAR_CONTAINER_PADDING = Padding.of(0);
    public static final int DEFAULT_SCROLL_BAR_SPACING = 4;
    public static final int DEFAULT_SCROLLER_WIDTH = 6;
    public static final int DEFAULT_MIN_SCROLLER_HEIGHT = 32;
    public static final int DEFAULT_ITEM_HEIGHT = 20;
    public static final int DEFAULT_ITEM_SPACING = 0;

    protected @Nullable ItemSprites itemSprites;
    protected int itemSpacing;
    protected @Nullable ResourceLocation listBackground;
    protected Border listBorder = Border.ZERO;
    protected Padding listPadding = Padding.ZERO;
    protected boolean scrolling;
    protected boolean scrollBarAlwaysVisible;
    protected int scrollBarSpacing;
    protected ScrollBarStyle scrollBarStyle = ScrollBarStyle.DETACHED;
    protected int scrollerWidth = 6;
    protected int minScrollerHeight = 32;
    protected @Nullable ScrollerSprites scrollerSprites;
    protected @Nullable ResourceLocation scrollBarBackground;
    protected Border scrollBarBorder = Border.ZERO;
    protected Padding scrollBarPadding = Padding.ZERO;
    protected Padding scrollBarContainerPadding = Padding.ZERO;
    protected @Nullable Supplier<Boolean> activeSupplier;

    /**
     * Constructs a FrameworkSelectionList with specified dimensions, position, and item height.
     * This constructor is only for subclassing. To create a {@link FrameworkSelectionList},
     * use {@link #builder()} to start building a new instance.
     *
     * @param width      the width of the list
     * @param height     the height of the list
     * @param x          the x position of the list
     * @param y          the y position of the list
     * @param itemHeight the height of each item in the list
     */
    protected FrameworkSelectionList(int width, int height, int x, int y, int itemHeight)
    {
        super(Minecraft.getInstance(), width, height, y, itemHeight);
        this.setPosition(x, y);
    }

    private FrameworkSelectionList(int x, int y, int width, int height, int itemHeight, @Nullable ItemSprites itemSprites, int itemSpacing, @Nullable ResourceLocation listBackground, Border listBorder, Padding listPadding, boolean scrollBarAlwaysVisible, int scrollBarSpacing, ScrollBarStyle scrollBarStyle, @Nullable ScrollerSprites scrollerSprites, int scrollerWidth, int minScrollerHeight, @Nullable ResourceLocation scrollBarBackground, Border scrollBarBorder, Padding scrollBarPadding, Padding scrollBarContainerPadding, @Nullable Supplier<Boolean> activeSupplier, @Nullable Consumer<Consumer<Item>> itemsSupplier)
    {
        this(width, height, x, y, itemHeight);
        this.itemSprites = itemSprites;
        this.itemSpacing = itemSpacing;
        this.listBackground = listBackground;
        this.listBorder = listBorder;
        this.listPadding = listPadding;
        this.scrollBarAlwaysVisible = scrollBarAlwaysVisible;
        this.scrollBarSpacing = scrollBarSpacing;
        this.scrollBarStyle = scrollBarStyle;
        this.scrollerSprites = scrollerSprites;
        this.scrollerWidth = scrollerWidth;
        this.minScrollerHeight = minScrollerHeight;
        this.scrollBarBackground = scrollBarBackground;
        this.scrollBarBorder = scrollBarBorder;
        this.scrollBarPadding = scrollBarPadding;
        this.scrollBarContainerPadding = scrollBarContainerPadding;
        this.activeSupplier = activeSupplier;
        if(itemsSupplier != null)
        {
            itemsSupplier.accept(this::addItem);
        }
    }

    @Override
    public void setPosition(int x, int y)
    {
        super.setPosition(x, y);
        this.setSize(this.width, this.height);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY)
    {
        return mouseY >= this.getY() && mouseY <= this.getY() + this.getHeight() && mouseX >= this.getX() && mouseX <= this.getX() + this.getWidth();
    }

    @Override
    public int getRowWidth()
    {
        return this.getRowRight() - this.getRowLeft();
    }

    @Override
    public int getRowLeft()
    {
        return this.getX() + this.listBorder.left() + this.listPadding.left();
    }

    @Override
    public int getRowRight()
    {
        if(this.getMaxScroll() > 0 || this.scrollBarAlwaysVisible)
        {
            int scrollBarArea = switch(this.scrollBarStyle) {
                case DETACHED -> this.listPadding.right() + this.listBorder.right() + this.scrollBarSpacing + this.scrollBarContainerPadding.left() + this.scrollBarBorder.left() + this.scrollBarPadding.left() + this.scrollerWidth + this.scrollBarPadding.right() + this.scrollBarBorder.right() + this.scrollBarContainerPadding.right();
                case MERGED -> this.scrollBarSpacing + this.scrollBarContainerPadding.left() + this.scrollBarBorder.left() + this.scrollBarPadding.left() + this.scrollerWidth + this.scrollBarPadding.right() + this.scrollBarBorder.right() + this.scrollBarContainerPadding.right() + this.listPadding.right() + this.listBorder.right();
            };
            return this.getX() + this.getWidth() - scrollBarArea;
        }
        return this.getX() + this.getWidth() - this.listPadding.right() - this.listBorder.right();
    }

    @Override
    protected int getRowTop(int index)
    {
        return this.getY() + this.listBorder.top() + this.listPadding.top() - (int) this.getScrollAmount() + index * this.itemHeight + index * this.itemSpacing;
    }

    @Override
    protected int getScrollbarPosition()
    {
        int offset = switch(this.scrollBarStyle) {
            case DETACHED -> this.scrollerWidth + this.scrollBarPadding.right() + this.scrollBarBorder.right() + this.scrollBarContainerPadding.right();
            case MERGED -> this.scrollerWidth + this.scrollBarPadding.right() + this.scrollBarBorder.right() + this.scrollBarContainerPadding.right() + this.listPadding.right() + this.listBorder.right();
        };
        return this.getX() + this.getWidth() - offset;
    }

    protected int getScrollbarHeight()
    {
        int scrollAreaHeight = this.getScrollAreaHeight();
        int scrollBarHeight = (int) (Mth.square(scrollAreaHeight) / (float) this.getMaxPosition());
        return Mth.clamp(scrollBarHeight, this.minScrollerHeight, scrollAreaHeight);
    }

    protected int getScrollAreaHeight()
    {
        int offset = switch(this.scrollBarStyle) {
            case DETACHED -> this.scrollBarContainerPadding.top() + this.scrollBarBorder.top() + this.scrollBarPadding.top() + this.scrollBarPadding.bottom() + this.scrollBarBorder.bottom() + this.scrollBarContainerPadding.bottom();
            case MERGED -> this.listBorder.top() + this.listPadding.top() + this.scrollBarContainerPadding.top() + this.scrollBarBorder.top() + this.scrollBarPadding.top() + this.scrollBarPadding.bottom() + this.scrollBarBorder.bottom() + this.scrollBarContainerPadding.bottom() + this.listPadding.bottom() + this.listBorder.bottom();
        };
        return this.getHeight() - offset;
    }

    protected int getScrollAreaTop()
    {
        int offset = switch(this.scrollBarStyle) {
            case DETACHED -> this.scrollBarContainerPadding.top() + this.scrollBarBorder.top() + this.scrollBarPadding.top();
            case MERGED -> this.listBorder.top() + this.listPadding.top() + this.scrollBarContainerPadding.top() + this.scrollBarBorder.top() + this.scrollBarPadding.top();
        };
        return this.getY() + offset;
    }

    @Override
    public int getMaxScroll()
    {
        return Math.max(0, this.getMaxPosition() - this.height + this.listBorder.top() + this.listPadding.top() + this.listPadding.bottom() + this.listBorder.bottom());
    }

    @Override
    protected int getMaxPosition()
    {
        return this.getItemCount() * (this.itemHeight + this.itemSpacing) - this.itemSpacing;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        if(this.activeSupplier != null)
        {
            this.active = this.activeSupplier.get();
        }
        this.renderListBackground(graphics, mouseX, mouseY, partialTick);
        this.renderListItems(graphics, mouseX, mouseY, partialTick);
        this.renderScrollBar(graphics, mouseX, mouseY, partialTick);
    }

    protected int getListBackgroundWidth()
    {
        return switch(this.scrollBarStyle) {
            case DETACHED -> this.listBorder.left() + this.listPadding.left() + this.getRowWidth() + this.listPadding.right() + this.listBorder.right();
            case MERGED -> this.getWidth();
        };
    }

    protected int getListBackgroundHeight()
    {
        return this.getHeight();
    }

    protected void renderListBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        // Draw outlines and background
        if(this.listBackground != null)
        {
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            graphics.blitSprite(this.listBackground, this.getX(), this.getY(), this.getListBackgroundWidth(), this.getListBackgroundHeight());
            RenderSystem.disableBlend();
        }
    }

    protected void renderScrollBar(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        int maxScroll = this.getMaxScroll();
        if(maxScroll > 0 || this.scrollBarAlwaysVisible)
        {
            // Draw a background behind the scroll bar
            if(this.scrollBarBackground != null)
            {
                RenderSystem.enableBlend();
                RenderSystem.enableDepthTest();
                int scrollBarTop = this.getY();
                if(this.scrollBarStyle == ScrollBarStyle.MERGED)
                    scrollBarTop += this.listBorder.top() + this.listPadding.top() + this.scrollBarContainerPadding.top();
                int scrollBarLeft = this.getScrollbarPosition() - this.scrollBarPadding.left() - this.scrollBarBorder.left();
                int scrollBarAreaWidth = this.scrollBarBorder.left() + this.scrollBarPadding.left() + this.scrollerWidth + this.scrollBarPadding.right() + this.scrollBarBorder.right();
                int scrollBarAreaHeight = this.getHeight() - this.scrollBarContainerPadding.top() - this.scrollBarContainerPadding.bottom();
                if(this.scrollBarStyle == ScrollBarStyle.MERGED)
                    scrollBarAreaHeight -= this.listBorder.top() + this.listPadding.top() + this.listPadding.bottom() + this.listBorder.bottom();
                graphics.blitSprite(this.scrollBarBackground, scrollBarLeft, scrollBarTop, scrollBarAreaWidth, scrollBarAreaHeight);
                RenderSystem.disableBlend();
            }

            // Draw scroll bar
            boolean scrollBarEnabled = maxScroll > 0 && this.isActive();
            int scrollBarStart = this.getScrollbarPosition();
            int scrollBarEnd = scrollBarStart + this.scrollerWidth;
            int scrollBarHeight = this.getScrollbarHeight();
            int scrollBarTop = (int) (this.getScrollAreaTop() + (this.getScrollAreaHeight() - this.getScrollbarHeight()) * (this.getScrollAmount() / Math.max(maxScroll, 1)));
            boolean scrollBarHovered = ClientUtils.isPointInArea(mouseX, mouseY, scrollBarStart, scrollBarTop, this.scrollerWidth, scrollBarHeight);
            if(this.scrollerSprites != null)
            {
                ResourceLocation sprite = this.scrollerSprites.get(scrollBarEnabled, scrollBarHovered, this.scrolling);
                if(sprite != null)
                {
                    RenderSystem.enableBlend();
                    RenderSystem.enableDepthTest();
                    graphics.setColor(1, 1, 1, this.active ? 1.0F : 0.5F);
                    graphics.blitSprite(sprite, scrollBarStart, scrollBarTop, scrollBarEnd - scrollBarStart, scrollBarHeight);
                    graphics.setColor(1, 1, 1, 1);
                    RenderSystem.disableBlend();
                }
            }
            else
            {
                // Fallback
                int scrollBarColour = scrollBarHovered ? 0xFF332E2D : 0xFF47403E;
                graphics.fill(scrollBarStart, scrollBarTop, scrollBarEnd, scrollBarTop + scrollBarHeight, scrollBarColour);
            }
        }
    }

    @Override
    protected void renderListItems(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        graphics.enableScissor(this.getRowLeft(), this.getY() + this.listBorder.top(), this.getRowRight(), this.getY() + this.getHeight() - this.listBorder.bottom());

        int rowLeft = this.getRowLeft();
        int rowWidth = this.getRowWidth();
        int rowHeight = this.itemHeight;
        int rowCount = this.getItemCount();

        // For efficiency, find the index to start drawing based on scroll amount
        int startIndex = Math.max(0, (int) ((this.getScrollAmount() - this.listPadding.top()) / (rowHeight + this.itemSpacing))); // TODO test
        for(int i = startIndex; i < rowCount; i++)
        {
            int rowTop = this.getRowTop(i);
            if(rowTop <= this.getY() + this.getHeight())
            {
                boolean hovered = !this.scrolling && ClientUtils.isPointInArea(mouseX, mouseY, rowLeft, rowTop, rowWidth, rowHeight);
                boolean selected = this.isSelectedItem(i);
                Item item = this.getEntry(i);
                item.setHovered(hovered);
                item.renderBackground(this.itemSprites, graphics, i, rowLeft, rowTop, rowWidth, rowHeight, mouseX, mouseY, hovered, selected);
                item.render(graphics, i, rowTop, rowLeft, rowWidth, rowHeight, mouseX, mouseY, selected, partialTick);
                continue;
            }
            // Break if the item is below the content area. Also stops drawing subsequent items.
            break;
        }

        graphics.disableScissor();
    }

    @Override
    protected void renderSelection(GuiGraphics graphics, int top, int rowWidth, int rowHeight, int outlineColour, int innerColour) {}

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if(!this.active || !this.isValidMouseClick(button))
            return false;

        this.updateScrollingState(mouseX, mouseY, button);
        if(!this.isMouseOver(mouseX, mouseY))
            return false;

        Item item = this.getEntry(mouseX, mouseY);
        if(item != null)
        {
            if(item.mouseClicked(mouseX, mouseY, button))
            {
                Item focused = this.getFocused();
                if(focused != item && focused instanceof ContainerEventHandler handler)
                {
                    handler.setFocused(null);
                }
                this.setFocused(item);
                this.setDragging(true);
                return true;
            }
        }
        return this.scrolling;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        this.scrolling = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY)
    {
        if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT)
        {
            if(this.getFocused() != null && this.isDragging() && this.getFocused().mouseDragged(mouseX, mouseY, button, deltaX, deltaY))
            {
                return true;
            }
            if(this.scrolling)
            {
                double unitsPerScroll = (double) this.getMaxScroll() / Math.max(1, this.getScrollAreaHeight() - this.getScrollbarHeight());
                this.setScrollAmount(this.getScrollAmount() + deltaY * unitsPerScroll);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY)
    {
        return this.active && super.mouseScrolled(mouseX, mouseY, deltaX, deltaY);
    }

    public void addItem(Item item)
    {
        super.addEntry(item);
    }

    @Nullable
    public Item removeItem(int index)
    {
        if(index >= 0 && index < this.children().size())
        {
            Item removed = super.remove(index);
            if(removed != null) this.clampScrollAmount();
            return removed;
        }
        return null;
    }

    public boolean removeItem(Item item)
    {
        boolean result = super.removeEntry(item);
        if(result) this.clampScrollAmount();
        return result;
    }

    public void removeIf(Predicate<? super Item> predicate)
    {
        if(this.children().removeIf(predicate))
        {
            this.clampScrollAmount();
        }
    }

    public Item getEntry(double mouseX, double mouseY)
    {
        int contentLeft = this.getX() + this.listBorder.left() + this.listPadding.left();
        int contentTop = this.getY() + this.listBorder.top();
        int contentWidth = this.getRowWidth();
        int contentHeight = this.getHeight() - this.listBorder.top() - this.listBorder.bottom();
        if(ClientUtils.isPointInArea((int) mouseX, (int) mouseY, contentLeft, contentTop, contentWidth, contentHeight))
        {
            int rowLeft = this.getRowLeft();
            int rowWidth = this.getRowWidth();
            int rowHeight = this.itemHeight;
            int rowCount = this.getItemCount();
            int startIndex = Math.max(0, (int) ((this.getScrollAmount() - this.listPadding.top()) / (rowHeight + this.itemSpacing)));
            for(int i = startIndex; i < rowCount; i++)
            {
                int rowTop = this.getRowTop(i);
                if(rowTop <= this.getY() + this.getHeight())
                {
                    if(ClientUtils.isPointInArea((int) mouseX, (int) mouseY, rowLeft, rowTop, rowWidth, rowHeight))
                    {
                        return this.getEntry(i);
                    }
                    continue;
                }
                break;
            }
        }
        return null;
    }

    @Override
    protected void updateScrollingState(double mouseX, double mouseY, int button)
    {
        this.scrolling = this.getMaxScroll() > 0 && button == GLFW.GLFW_MOUSE_BUTTON_LEFT && ClientUtils.isPointInArea((int) mouseX, (int) mouseY, this.getScrollbarPosition(), this.getScrollAreaTop(), this.scrollerWidth, this.getScrollAreaHeight());
        ClientServices.CLIENT.setScrollingState(this, this.scrolling);
    }

    @Override
    @Nullable
    public Item getSelected()
    {
        Item selected = super.getSelected();
        if(selected != null && selected.isSelectable())
        {
            return selected;
        }
        return null;
    }

    @Override
    public void setSelected(@Nullable Item item)
    {
        if(item == null || item.isSelectable())
        {
            super.setSelected(item);
        }
    }

    public static abstract class Item extends ObjectSelectionList.Entry<Item>
    {
        private boolean hovered;

        protected abstract void renderContent(GuiGraphics graphics, int index, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, boolean selected, float partialTick);

        private void setHovered(boolean hovered)
        {
            this.hovered = hovered;
        }

        public boolean isSelectable()
        {
            return true;
        }

        @Override
        public Component getNarration()
        {
            return CommonComponents.EMPTY;
        }

        @Override
        public final void render(GuiGraphics graphics, int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean selected, float partialTick)
        {
            this.renderContent(graphics, index, x, y, width, height, mouseX, mouseY, this.hovered, selected, partialTick);
        }

        protected void renderBackground(@Nullable ItemSprites sprites, GuiGraphics graphics, int index, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, boolean selected)
        {
            if(sprites != null)
            {
                ResourceLocation sprite = sprites.getSprite(true, hovered, selected);
                if(sprite != null)
                {
                    graphics.blitSprite(sprite, x, y, width, height);
                }
            }
        }
    }

    public enum ScrollBarStyle
    {
        DETACHED, MERGED
    }

    /**
     * Creates a {@link Builder} for constructing {@link FrameworkSelectionList}s. The builder by
     * default is configured to replicate the visual style of vanilla's selection lists.
     *
     * @return a new {@link Builder} instance for creating a FrameworkSelectionList.
     */
    public static Builder builder()
    {
        return new Builder();
    }

    public static class Builder
    {
        private int x;
        private int y;
        private int width = 100;
        private int height = 100;
        private int itemHeight = DEFAULT_ITEM_HEIGHT;
        private @Nullable ItemSprites itemSprites = DEFAULT_ITEM_SPRITE;
        private int itemSpacing = DEFAULT_ITEM_SPACING;
        private @Nullable ResourceLocation listBackground = DEFAULT_BACKGROUND;
        private Border listBorder = DEFAULT_LIST_BORDER;
        private Padding listPadding = DEFAULT_LIST_PADDING;
        private boolean scrollBarAlwaysVisible;
        private int scrollBarSpacing = DEFAULT_SCROLL_BAR_SPACING;
        private ScrollBarStyle scrollBarStyle = ScrollBarStyle.DETACHED;
        private @Nullable ScrollerSprites scrollerSprites = DEFAULT_SCROLLER_SPRITE;
        private int scrollerWidth = DEFAULT_SCROLLER_WIDTH;
        private int minScrollerHeight = DEFAULT_MIN_SCROLLER_HEIGHT;
        private @Nullable ResourceLocation scrollBarBackground = DEFAULT_SCROLL_BAR_BACKGROUND;
        private Border scrollBarBorder = DEFAULT_SCROLL_BAR_BORDER;
        private Padding scrollBarPadding = DEFAULT_SCROLL_BAR_PADDING;
        private Padding scrollBarContainerPadding = DEFAULT_SCROLL_BAR_CONTAINER_PADDING;
        private @Nullable Supplier<Boolean> activeSupplier;
        private @Nullable Consumer<Consumer<Item>> itemsSupplier;

        private Builder() {}

        public FrameworkSelectionList build()
        {
            return new FrameworkSelectionList(this.x, this.y, this.width, this.height, this.itemHeight, this.itemSprites, this.itemSpacing, this.listBackground, this.listBorder, this.listPadding, this.scrollBarAlwaysVisible, this.scrollBarSpacing, this.scrollBarStyle, this.scrollerSprites, this.scrollerWidth, this.minScrollerHeight, this.scrollBarBackground, this.scrollBarBorder, this.scrollBarPadding, this.scrollBarContainerPadding, this.activeSupplier, this.itemsSupplier);
        }

        /**
         * Sets the x position of this list.
         *
         * @param x the x position in pixel units
         * @return this {@link Builder} for method chaining
         */
        public Builder setX(int x)
        {
            this.x = x;
            return this;
        }

        /**
         * Sets the y position of this list.
         *
         * @param y the y position in pixel units
         * @return this {@link Builder} for method chaining
         */
        public Builder setY(int y)
        {
            this.y = y;
            return this;
        }

        /**
         * Sets the x and y position of this list. This method is simply for convenience
         * to set both values in a single call.
         *
         * @param x the x position in pixel units
         * @param y the y position in pixel units
         * @return this {@link Builder} for method chaining
         */
        public Builder setPosition(int x, int y)
        {
            this.x = x;
            this.y = y;
            return this;
        }

        /**
         * Sets the width of this list.
         *
         * @param width the width in pixel units
         * @return this {@link Builder} for method chaining
         */
        public Builder setWidth(int width)
        {
            this.width = width;
            return this;
        }

        /**
         * Sets the height of this list.
         *
         * @param height the height in pixel units
         * @return this {@link Builder} for method chaining
         */
        public Builder setHeight(int height)
        {
            this.height = height;
            return this;
        }

        /**
         * Sets the width and height (the size) of this list. This method is simply for
         * convenience to set both values in a single call.
         *
         * @param width  the width in pixel units
         * @param height the height in pixel units
         * @return this {@link Builder} for method chaining
         */
        public Builder setSize(int width, int height)
        {
            this.width = width;
            this.height = height;
            return this;
        }

        /**
         * Sets the height of individual items in the list.
         *
         * @param height the item height in pixel units
         * @return this {@link Builder} for method chaining
         */
        public Builder setItemHeight(int height)
        {
            this.itemHeight = height;
            return this;
        }

        /**
         * Sets the {@link ItemSprites} used to render list items.
         *
         * @param sprites the item sprites to use, or null to disable sprites
         * @return this {@link Builder} for method chaining
         */
        public Builder setItemSprites(@Nullable ItemSprites sprites)
        {
            this.itemSprites = sprites;
            return this;
        }

        /**
         * Disables item sprites for the list.
         *
         * @return this {@link Builder} for method chaining
         */
        public Builder noItemSprites()
        {
            this.itemSprites = null;
            return this;
        }

        /**
         * Sets the spacing between items in the list.
         *
         * @param spacing the spacing between items in pixel units
         * @return this {@link Builder} for method chaining
         */
        public Builder setItemSpacing(int spacing)
        {
            this.itemSpacing = spacing;
            return this;
        }

        /**
         * Sets the background sprite for the list.
         *
         * @param texture the background resource location, or null for no background
         * @return this {@link Builder} for method chaining
         */
        public Builder setListBackground(@Nullable ResourceLocation texture)
        {
            this.listBackground = texture;
            return this;
        }

        /**
         * Disables the background sprite for the list.
         *
         * @return this {@link Builder} for method chaining
         */
        public Builder noListBackground()
        {
            this.listBackground = null;
            return this;
        }

        /**
         * Sets the border applied to the outer edges of the list.
         *
         * @param border the border to apply to the list
         * @return this {@link Builder} for method chaining
         */
        public Builder setListBorder(Border border)
        {
            this.listBorder = border;
            return this;
        }

        /**
         * Sets the padding applied inside the list, between its border and content.
         *
         * @param padding the padding to apply inside the list
         * @return this {@link Builder} for method chaining
         */
        public Builder setListPadding(Padding padding)
        {
            this.listPadding = padding;
            return this;
        }

        /**
         * Sets whether the scroll bar is always visible, even when there is not enough items in the
         * list to even warrant scrolling.
         *
         * @param alwaysVisible true to always show the scroll bar
         * @return this {@link Builder} for method chaining
         */
        public Builder setScrollBarAlwaysVisible(boolean alwaysVisible)
        {
            this.scrollBarAlwaysVisible = alwaysVisible;
            return this;
        }

        /**
         * Sets the spacing between the list content and the scroll bar container.
         *
         * @param spacing the spacing in pixel units
         * @return this {@link Builder} for method chaining
         */
        public Builder setScrollBarSpacing(int spacing)
        {
            this.scrollBarSpacing = spacing;
            return this;
        }

        /**
         * Sets the {@link ScrollBarStyle} used to render the scroll bar. See {@link ScrollBarStyle}
         * for the available styles.
         *
         * @param style the scroll bar style to use
         * @return this {@link Builder} for method chaining
         */
        public Builder setScrollBarStyle(ScrollBarStyle style)
        {
            this.scrollBarStyle = style;
            return this;
        }

        /**
         * Sets the {@link ScrollerSprites} used for rendering the scroller in the scroll bar
         *
         * @param sprites the sprites to use for the scroller, or null to disable
         * @return this {@link Builder} for method chaining
         */
        public Builder setScrollerSprites(@Nullable ScrollerSprites sprites)
        {
            this.scrollerSprites = sprites;
            return this;
        }

        /**
         * Sets the width of the scroller in the scroll bar
         *
         * @param width the width in pixel units
         * @return this {@link Builder} for method chaining
         */
        public Builder setScrollerWidth(int width)
        {
            this.scrollerWidth = width;
            return this;
        }

        /**
         * Sets the min height of the scroller in the scroll bar
         *
         * @param minHeight the minimum height in pixel units
         * @return this {@link Builder} for method chaining
         */
        public Builder setMinScrollerHeight(int minHeight)
        {
            this.minScrollerHeight = minHeight;
            return this;
        }

        /**
         * Sets the sprite to use for the background of the scroll bar
         *
         * @param texture the resource location to a sprite, or null for no background
         * @return this {@link Builder} for method chaining
         */
        public Builder setScrollBarBackground(@Nullable ResourceLocation texture)
        {
            this.scrollBarBackground = texture;
            return this;
        }

        /**
         * Sets the border applied to the scroll bar
         *
         * @param border the border to apply to the scroll bar
         * @return this {@link Builder} for method chaining
         */
        public Builder setScrollBarBorder(Border border)
        {
            this.scrollBarBorder = border;
            return this;
        }

        /**
         * Sets the padding applied inside the scroll bar, between its border and scroller
         *
         * @param padding the padding to apply inside the scroll bar
         * @return this {@link Builder} for method chaining
         */
        public Builder setScrollBarPadding(Padding padding)
        {
            this.scrollBarPadding = padding;
            return this;
        }

        /**
         * Sets the padding applied around the scroll bar container
         *
         * @param padding the padding to apply around the scroll bar container
         * @return this {@link Builder} for method chaining
         */
        public Builder setScrollBarContainerPadding(Padding padding)
        {
            this.scrollBarContainerPadding = padding;
            return this;
        }

        /**
         * Sets a dependency on an arbitrary boolean. This will update the
         * {@link AbstractWidget#active} property of the list.
         *
         * @param active a supplier returning a boolean representing the dependent state
         * @return this {@link Builder} for method chaining
         */
        public Builder setDependent(Supplier<Boolean> active)
        {
            this.activeSupplier = active;
            return this;
        }

        /**
         * Sets the initial items for the list. These will be added to the list during call to
         * {@link #build()}. Items should be added via the consumer provided in the consumer.
         *
         * @param items a consumer used to populate the initial list items
         * @return this {@link Builder} for method chaining
         */
        public Builder setInitialItems(Consumer<Consumer<Item>> items)
        {
            this.itemsSupplier = items;
            return this;
        }
    }

    /**
     * Represents a collection of sprites tied to specific enabled, disabled, hovered, and
     * selected states of list items.
     */
    public static final class ItemSprites
    {
        private final ImmutableMap<Integer, ResourceLocation> map;

        private ItemSprites(
                @Nullable ResourceLocation enabled,
                @Nullable ResourceLocation disabled,
                @Nullable ResourceLocation enabledHovered,
                @Nullable ResourceLocation disabledHovered,
                @Nullable ResourceLocation enabledSelected,
                @Nullable ResourceLocation disabledSelected,
                @Nullable ResourceLocation enabledHoveredSelected,
                @Nullable ResourceLocation disabledHoveredSelected)
        {
            ImmutableMap.Builder<Integer, ResourceLocation> builder = ImmutableMap.builder();
            if(disabled != null) builder.put(this.calculateKey(false, false, false), disabled);
            if(enabled != null) builder.put(this.calculateKey(true, false, false), enabled);
            if(disabledHovered != null) builder.put(this.calculateKey(false, true, false), disabledHovered);
            if(enabledHovered != null) builder.put(this.calculateKey(true, true, false), enabledHovered);
            if(disabledSelected != null) builder.put(this.calculateKey(false, false, true), disabledSelected);
            if(enabledSelected != null) builder.put(this.calculateKey(true, false, true), enabledSelected);
            if(disabledHoveredSelected != null) builder.put(this.calculateKey(false, true, true), disabledHoveredSelected);
            if(enabledHoveredSelected != null) builder.put(this.calculateKey(true, true, true), enabledHoveredSelected);
            this.map = builder.build();
        }

        private int calculateKey(boolean enabled, boolean hovered, boolean selected)
        {
            int key = 0;
            if(enabled) key |= 1;
            if(hovered) key |= 1 << 1;
            if(selected) key |= 1 << 2;
            return key;
        }

        /**
         * Retrieves the sprite texture resource location based on the provided state parameters.
         *
         * @param enabled  indicates the enabled state
         * @param hovered  indicates the hovered state
         * @param selected indicates the selected state
         * @return a {@link ResourceLocation} pointing to a texture based on the given states, or null
         */
        @Nullable
        public ResourceLocation getSprite(boolean enabled, boolean hovered, boolean selected)
        {
            return this.map.get(this.calculateKey(enabled, hovered, selected));
        }

        /**
         * Creates a new {@link ItemSprites} where all sprite states point to the same texture location.
         * Alternatively you can use the {@link Builder} to configure the sprites individually.
         *
         * @param all the resource location to be used for all sprite states; can be null
         * @return a new instance of {@link ItemSprites} with the same resource location for all states
         */
        public static ItemSprites of(@Nullable ResourceLocation all)
        {
            return new ItemSprites(all, all, all, all, all, all, all, all);
        }

        /**
         * Creates a new {@link ItemSprites} that matches the states of a vanilla button (enabled, disabled
         * and selected (focused)). Alternatively you can use the {@link Builder} to configure the sprites
         * individually.
         *
         * @param enabled         the resource location to a texture for the enabled state, or null for no texture
         * @param disabled        the resource location to a texture for the disabled state, or null for no texture
         * @param enabledSelected the resource location to a texture for the enabled and selected state, or null for no texture
         * @return a new {@link ItemSprites}  instance initialised with the provided resource locations
         */
        public static ItemSprites of(@Nullable ResourceLocation enabled, @Nullable ResourceLocation disabled, @Nullable ResourceLocation enabledSelected)
        {
            return new ItemSprites(enabled, disabled, enabled, disabled, enabledSelected, disabled, enabledSelected, disabled);
        }

        /**
         * @return A new {@link Builder} to configure and build an {@link ItemSprites} instance
         */
        public static Builder builder()
        {
            return new Builder();
        }

        public static class Builder
        {
            private @Nullable ResourceLocation enabled;
            private @Nullable ResourceLocation disabled;
            private @Nullable ResourceLocation enabledHovered;
            private @Nullable ResourceLocation disabledHovered;
            private @Nullable ResourceLocation enabledSelected;
            private @Nullable ResourceLocation disabledSelected;
            private @Nullable ResourceLocation enabledHoveredSelected;
            private @Nullable ResourceLocation disabledHoveredSelected;

            /**
             * Builds an {@link ItemSprites} instance with the configured sprites
             *
             * @return a new {@link ItemSprites} instance
             */
            public ItemSprites build()
            {
                return new ItemSprites(this.enabled, this.disabled, this.enabledHovered, this.disabledHovered, this.enabledSelected, this.disabledSelected, this.enabledHoveredSelected, this.disabledHoveredSelected);
            }

            /**
             * Sets the sprite for the enabled state
             *
             * @param texture the resource location to a sprite texture, or null
             * @return this {@link Builder} for method chaining
             */
            public Builder setEnabled(@Nullable ResourceLocation texture)
            {
                this.enabled = texture;
                return this;
            }

            /**
             * Sets the sprite for the disabled state.
             *
             * @param texture the resource location to a sprite texture, or null
             * @return this {@link Builder} for method chaining
             */
            public Builder setDisabled(@Nullable ResourceLocation texture)
            {
                this.disabled = texture;
                return this;
            }

            /**
             * Sets the sprite for the enabled and hovered state
             *
             * @param texture the resource location to a sprite texture, or null
             * @return this {@link Builder} for method chaining
             */
            public Builder setEnabledHovered(@Nullable ResourceLocation texture)
            {
                this.enabledHovered = texture;
                return this;
            }

            /**
             * Sets the sprite for the disabled and hovered state
             *
             * @param texture the resource location to a sprite texture, or null
             * @return this {@link Builder} for method chaining
             */
            public Builder setDisabledHovered(@Nullable ResourceLocation texture)
            {
                this.disabledHovered = texture;
                return this;
            }

            /**
             * Sets the sprite for the enabled and selected state
             *
             * @param texture the resource location to a sprite texture, or null
             * @return this {@link Builder} for method chaining
             */
            public Builder setEnabledSelected(@Nullable ResourceLocation texture)
            {
                this.enabledSelected = texture;
                return this;
            }

            /**
             * Sets the sprite for the disabled and selected state
             *
             * @param texture the resource location to a sprite texture, or null
             * @return this {@link Builder} for method chaining
             */
            public Builder setDisabledSelected(@Nullable ResourceLocation texture)
            {
                this.disabledSelected = texture;
                return this;
            }

            /**
             * Sets the sprite for the enabled, hovered, and selected state
             *
             * @param texture the resource location to a sprite texture, or null
             * @return this {@link Builder} for method chaining
             */
            public Builder setEnabledHoveredSelected(@Nullable ResourceLocation texture)
            {
                this.enabledHoveredSelected = texture;
                return this;
            }

            /**
             * Sets the sprite for the disabled, hovered, and selected state
             *
             * @param texture the resource location to a sprite texture, or null
             * @return this {@link Builder} for method chaining
             */
            public Builder setDisabledHoveredSelected(@Nullable ResourceLocation texture)
            {
                this.disabledHoveredSelected = texture;
                return this;
            }
        }
    }

    /**
     * Represents a set of sprites used for rendering the scroller in different visual states.
     * These states include enabled, disabled, hovered, and dragging. Disabled state may be null
     * to hide the scroller completely when a selection list is not active.
     */
    public static final class ScrollerSprites
    {
        private final ResourceLocation enabled;
        private final @Nullable ResourceLocation disabled;
        private final ResourceLocation hovered;
        private final ResourceLocation dragging;

        private ScrollerSprites(ResourceLocation enabled, @Nullable ResourceLocation disabled, ResourceLocation hovered, ResourceLocation dragging)
        {
            this.enabled = enabled;
            this.disabled = disabled;
            this.hovered = hovered;
            this.dragging = dragging;
        }

        /**
         * Creates a {@link ScrollerSprites} instance with all states using the same {@link ResourceLocation}.
         *
         * @param all the resource location to be used for all states (enabled, disabled, hovered, and dragging).
         * @return A new {@link ScrollerSprites} instance with all states set to the given resource location.
         */
        public static ScrollerSprites of(ResourceLocation all)
        {
            return new ScrollerSprites(all, all, all, all);
        }

        /**
         * Creates an instance of {@link ScrollerSprites} with the specified ResourceLocations for enabled,
         * disabled, and hovered states. The hovering state is also used for the dragging state.
         *
         * @param enabled  the resource location to a sprite for the enabled state.
         * @param disabled the resource location to a sprite for the disabled state.
         * @param hovered  the resource location to a sprite for the hovered state.
         * @return A new {@link ScrollerSprites} instance
         */
        public static ScrollerSprites of(ResourceLocation enabled, ResourceLocation disabled, ResourceLocation hovered)
        {
            return new ScrollerSprites(enabled, disabled, hovered, hovered);
        }

        /**
         * Creates an instance of {@link ScrollerSprites} with the specified ResourceLocations for enabled,
         * disabled, hovered, and dragging states.
         *
         * @param enabled  the resource location to a sprite for the enabled state.
         * @param disabled the resource location to a sprite for the disabled state.
         * @param hovered  the resource location to a sprite for the hovered state.
         * @param dragging the resource location to a sprite for the dragging state.
         * @return A new {@link ScrollerSprites} instance
         */
        public static ScrollerSprites of(ResourceLocation enabled, ResourceLocation disabled, ResourceLocation hovered, ResourceLocation dragging)
        {
            return new ScrollerSprites(enabled, disabled, hovered, dragging);
        }

        /**
         * Retrieves the appropriate {@link ResourceLocation} based on the given state.
         *
         * @param enabled  true if the scroller is enabled
         * @param hovered  true if the scroller is being hovered by the cursor
         * @param dragging true if the scroller is currently being dragged
         * @return The {@link ResourceLocation}  corresponding to the specified state.
         */
        @Nullable
        public ResourceLocation get(boolean enabled, boolean hovered, boolean dragging)
        {
            if(!enabled) return this.disabled;
            if(hovered) return this.hovered;
            if(dragging) return this.dragging;
            return this.enabled;
        }
    }
}
