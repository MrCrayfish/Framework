package com.mrcrayfish.framework.api.client.screen.widget;

import com.google.common.annotations.Beta;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mrcrayfish.framework.api.client.screen.ItemSprites;
import com.mrcrayfish.framework.api.client.screen.widget.layout.Border;
import com.mrcrayfish.framework.api.client.screen.widget.layout.Padding;
import com.mrcrayfish.framework.client.ClientUtils;
import com.mrcrayfish.framework.platform.ClientServices;
import com.mrcrayfish.framework.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.function.Predicate;
import java.util.function.Supplier;

@Beta
public class FrameworkSelectionList extends ObjectSelectionList<FrameworkSelectionList.Item>
{
    public static final ResourceLocation DEFAULT_BACKGROUND = Utils.rl("widget/selection_list/background");
    public static final ItemSprites DEFAULT_ITEM_SPRITE = ItemSprites.builder()
        .setEnabledSelected(Utils.rl("widget/selection_list/item_enabled_selected"))
        .setEnabledHoveredSelected(Utils.rl("widget/selection_list/item_enabled_selected"))
        .build();
    public static final Padding DEFAULT_LIST_PADDING = Padding.of(4);
    public static final Border DEFAULT_LIST_BORDER = Border.of(0);
    public static final ItemSprites DEFAULT_SCROLLER_SPRITE = ItemSprites.of(ResourceLocation.withDefaultNamespace("widget/scroller"));
    public static final ResourceLocation DEFAULT_SCROLL_BAR_BACKGROUND = ResourceLocation.withDefaultNamespace("widget/scroller_background");
    public static final Padding DEFAULT_SCROLL_BAR_PADDING = Padding.of(0);
    public static final Border DEFAULT_SCROLL_BAR_BORDER = Border.of(0);
    public static final Padding DEFAULT_SCROLL_BAR_CONTAINER_PADDING = Padding.of(0);

    protected @Nullable ItemSprites itemSprites = DEFAULT_ITEM_SPRITE;
    protected int itemSpacing = 0;
    protected @Nullable ResourceLocation listBackground = DEFAULT_BACKGROUND;
    protected Border listBorder = DEFAULT_LIST_BORDER;
    protected Padding listPadding = DEFAULT_LIST_PADDING;
    protected boolean scrolling;
    protected boolean scrollBarAlwaysVisible;
    protected int scrollBarSpacing = 4;
    protected ScrollBarStyle scrollBarStyle = ScrollBarStyle.DETACHED;
    protected @Nullable ItemSprites scrollerSprites = DEFAULT_SCROLLER_SPRITE;
    protected int scrollerWidth = 6;
    protected int minScrollerHeight = 32;
    protected @Nullable ResourceLocation scrollBarBackground = DEFAULT_SCROLL_BAR_BACKGROUND;
    protected Border scrollBarBorder = DEFAULT_SCROLL_BAR_BORDER;
    protected Padding scrollBarPadding = DEFAULT_SCROLL_BAR_PADDING;
    protected Padding scrollBarContainerPadding = DEFAULT_SCROLL_BAR_CONTAINER_PADDING;
    protected @Nullable Supplier<Boolean> activeSupplier;

    // TODO set minm scroller height

    public FrameworkSelectionList(int width, int height, int x, int y, int itemHeight)
    {
        super(Minecraft.getInstance(), width, height, y, itemHeight);
        this.setPosition(x, y);
    }

    @Override
    public void setPosition(int x, int y)
    {
        super.setPosition(x, y);
        this.setSize(this.width, this.height);
    }

    public void setListBackground(@Nullable ResourceLocation background)
    {
        this.listBackground = background;
    }

    public void setListBorder(int size)
    {
        this.listBorder = Border.of(size);
    }

    public void setListBorder(int left, int top, int right, int bottom)
    {
        this.listBorder = Border.of(left, top, right, bottom);
    }

    public void setListBorder(Border border)
    {
        this.listBorder = border;
    }

    public void setListPadding(int padding)
    {
        this.listPadding = Padding.of(padding);
    }

    public void setListPadding(int left, int top, int right, int bottom)
    {
        this.listPadding = Padding.of(left, top, right, bottom);
    }

    public void setListPadding(Padding padding)
    {
        this.listPadding = padding;
    }

    public void setItemSpacing(int itemSpacing)
    {
        this.itemSpacing = itemSpacing;
    }

    public void setItemSprites(@Nullable ItemSprites sprites)
    {
        this.itemSprites = sprites;
    }

    public void setScrollerWidth(int width)
    {
        this.scrollerWidth = width;
    }

    public void setScrollerMinHeight(int minHeight)
    {
        this.minScrollerHeight = minHeight;
    }

    public void setScrollerSprites(@Nullable ItemSprites sprites)
    {
        this.scrollerSprites = sprites;
    }

    public void setScrollBarBackground(@Nullable ResourceLocation background)
    {
        this.scrollBarBackground = background;
    }

    public void setScrollBarBorder(int size)
    {
        this.scrollBarBorder = Border.of(size);
    }

    public void setScrollBarBorder(int left, int top, int right, int bottom)
    {
        this.scrollBarBorder = Border.of(left, top, right, bottom);
    }

    public void setScrollBarBorder(Border border)
    {
        this.scrollBarBorder = border;
    }

    public void setScrollBarStyle(ScrollBarStyle scrollBarStyle)
    {
        this.scrollBarStyle = scrollBarStyle;
    }

    public void setScrollBarAlwaysVisible(boolean scrollBarAlwaysVisible)
    {
        this.scrollBarAlwaysVisible = scrollBarAlwaysVisible;
    }

    public void setScrollBarSpacing(int scrollBarSpacing)
    {
        this.scrollBarSpacing = scrollBarSpacing;
    }

    public void setScrollBarPadding(int padding)
    {
        this.scrollBarPadding = Padding.of(padding);
    }

    public void setScrollBarPadding(int left, int top, int right, int bottom)
    {
        this.scrollBarPadding = Padding.of(left, top, right, bottom);
    }

    public void setScrollBarContainerPadding(int size)
    {
        this.scrollBarContainerPadding = Padding.of(size);
    }

    public void setScrollBarContainerPadding(int left, int top, int right, int bottom)
    {
        this.scrollBarContainerPadding = Padding.of(left, top, right, bottom);
    }

    public void setScrollBarContainerPadding(Padding padding)
    {
        this.scrollBarContainerPadding = padding;
    }

    public void setActive(@Nullable Supplier<Boolean> activeSupplier)
    {
        this.activeSupplier = activeSupplier;
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
            boolean scrollBarEnabled = maxScroll > 0;
            int scrollBarStart = this.getScrollbarPosition();
            int scrollBarEnd = scrollBarStart + this.scrollerWidth;
            int scrollBarHeight = this.getScrollbarHeight();
            int scrollBarTop = (int) (this.getScrollAreaTop() + (this.getScrollAreaHeight() - this.getScrollbarHeight()) * (this.getScrollAmount() / Math.max(maxScroll, 1)));
            boolean scrollBarHovered = ClientUtils.isPointInArea(mouseX, mouseY, scrollBarStart, scrollBarTop, this.scrollerWidth, scrollBarHeight);
            if(this.scrollerSprites != null)
            {
                ResourceLocation sprite = this.scrollerSprites.getSprite(scrollBarEnabled, scrollBarHovered, this.scrolling);
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
}
