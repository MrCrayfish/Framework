package com.mrcrayfish.framework.api.client.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mrcrayfish.framework.api.client.screen.ItemSprites;
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

public class FrameworkSelectionList extends ObjectSelectionList<FrameworkSelectionList.Item>
{
    public static final ResourceLocation DEFAULT_BACKGROUND = Utils.rl("widget/selection_list/background");
    public static final ItemSprites DEFAULT_ITEM_SPRITE = new ItemSprites(null, null, null, null, Utils.rl("widget/selection_list/item_enabled_selected"), null, Utils.rl("widget/selection_list/item_enabled_selected"), null);
    public static final ItemSprites DEFAULT_SCROLLER_SPRITE = new ItemSprites(ResourceLocation.withDefaultNamespace("widget/scroller"));
    public static final ResourceLocation DEFAULT_SCROLL_BAR_BACKGROUND = ResourceLocation.withDefaultNamespace("widget/scroller_background");
    protected static final Padding DEFAULT_ITEM_CONTAINER_PADDING = new Padding(4);
    protected static final Padding DEFAULT_SCROLL_BAR_PADDING = new Padding(0);

    protected Padding itemContainerPadding = DEFAULT_ITEM_CONTAINER_PADDING;
    protected @Nullable ItemSprites itemSprites = DEFAULT_ITEM_SPRITE;
    protected int itemSpacing = 0;
    protected @Nullable ResourceLocation listBackground = DEFAULT_BACKGROUND;
    protected int listBackgroundBorder = 0;
    protected boolean scrolling;
    protected int scrollBarWidth = 6;
    protected boolean scrollBarAlwaysVisible;
    protected int scrollBarSpacing = 4;
    protected @Nullable ItemSprites scrollBarSprites = DEFAULT_SCROLLER_SPRITE;
    protected @Nullable ResourceLocation scrollBarBackground = DEFAULT_SCROLL_BAR_BACKGROUND;
    protected int scrollBarBackgroundBorder = 0;
    protected ScrollBarStyle scrollBarStyle = ScrollBarStyle.DETACHED;
    protected Padding scrollBarPadding = DEFAULT_SCROLL_BAR_PADDING;
    protected @Nullable Supplier<Boolean> activeSupplier;

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

    public void setListBackgroundBorder(int listBackgroundBorder)
    {
        this.listBackgroundBorder = listBackgroundBorder;
    }

    public void setItemContainerPadding(int padding)
    {
        this.itemContainerPadding = new Padding(padding);
    }

    public void setItemContainerPadding(int left, int top, int right, int bottom)
    {
        this.itemContainerPadding = new Padding(left, top, right, bottom);
    }

    public void setItemSpacing(int itemSpacing)
    {
        this.itemSpacing = itemSpacing;
    }

    public void setItemSprites(@Nullable ItemSprites sprites)
    {
        this.itemSprites = sprites;
    }

    public void setScrollBarWidth(int scrollBarWidth)
    {
        this.scrollBarWidth = scrollBarWidth;
    }

    public void setScrollBarSprites(@Nullable ItemSprites sprites)
    {
        this.scrollBarSprites = sprites;
    }

    public void setScrollBarBackground(@Nullable ResourceLocation background)
    {
        this.scrollBarBackground = background;
    }

    public void setScrollBarBackgroundBorder(int scrollBarBackgroundBorder)
    {
        this.scrollBarBackgroundBorder = scrollBarBackgroundBorder;
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
        this.scrollBarPadding = new Padding(padding);
    }

    public void setScrollBarPadding(int left, int top, int right, int bottom)
    {
        this.scrollBarPadding = new Padding(left, top, right, bottom);
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
        return this.getX() + this.listBackgroundBorder + this.itemContainerPadding.left;
    }

    @Override
    public int getRowRight()
    {
        if(this.getMaxScroll() > 0 || this.scrollBarAlwaysVisible)
        {
            int scrollBarArea = switch(this.scrollBarStyle) {
                case DETACHED -> this.itemContainerPadding.right + this.listBackgroundBorder + this.scrollBarSpacing + this.scrollBarBackgroundBorder + this.scrollBarPadding.left + this.scrollBarWidth + this.scrollBarPadding.right + this.scrollBarBackgroundBorder;
                case MERGED -> this.scrollBarSpacing + this.scrollBarBackgroundBorder + this.scrollBarPadding.left + this.scrollBarWidth + this.scrollBarPadding.right + this.scrollBarBackgroundBorder + this.itemContainerPadding.right + this.listBackgroundBorder;
            };
            return this.getX() + this.getWidth() - scrollBarArea;
        }
        return this.getX() + this.getWidth() - this.itemContainerPadding.right - this.listBackgroundBorder;
    }

    @Override
    protected int getRowTop(int index)
    {
        return this.getY() + this.listBackgroundBorder + this.itemContainerPadding.top - (int) this.getScrollAmount() + index * this.itemHeight + index * this.itemSpacing;
    }

    @Override
    protected int getScrollbarPosition()
    {
        int offset = switch(this.scrollBarStyle) {
            case DETACHED -> this.scrollBarWidth + this.scrollBarPadding.right + this.scrollBarBackgroundBorder;
            case MERGED -> this.scrollBarWidth + this.scrollBarPadding.right + this.scrollBarBackgroundBorder + this.itemContainerPadding.right + this.listBackgroundBorder;
        };
        return this.getX() + this.getWidth() - offset;
    }

    protected int getScrollbarHeight()
    {
        int scrollAreaHeight = this.getScrollAreaHeight();
        int scrollBarHeight = (int) (Mth.square(scrollAreaHeight) / (float) this.getMaxPosition());
        return Mth.clamp(scrollBarHeight, 32, scrollAreaHeight);
    }

    protected int getScrollAreaHeight()
    {
        int offset = switch(this.scrollBarStyle) {
            case DETACHED -> this.scrollBarPadding.top + this.scrollBarPadding.bottom + this.scrollBarBackgroundBorder * 2;
            case MERGED -> this.scrollBarPadding.top + this.scrollBarPadding.bottom + this.scrollBarBackgroundBorder * 2 + this.itemContainerPadding.top + this.itemContainerPadding.bottom + + this.listBackgroundBorder * 2;
        };
        return this.getHeight() - offset;
    }

    protected int getScrollAreaTop()
    {
        int offset = switch(this.scrollBarStyle) {
            case DETACHED -> this.scrollBarPadding.top + this.scrollBarBackgroundBorder;
            case MERGED -> this.scrollBarPadding.top + this.scrollBarBackgroundBorder + this.itemContainerPadding.top + this.listBackgroundBorder;
        };
        return this.getY() + offset;
    }

    @Override
    public int getMaxScroll()
    {
        return Math.max(0, this.getMaxPosition() - this.height + this.itemContainerPadding.top + this.itemContainerPadding.bottom + this.listBackgroundBorder * 2);
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
            case DETACHED -> this.listBackgroundBorder + this.itemContainerPadding.left + this.getRowWidth() + this.itemContainerPadding.right + this.listBackgroundBorder;
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
                    scrollBarTop += this.listBackgroundBorder + this.itemContainerPadding.top;
                int scrollBarLeft = this.getScrollbarPosition() - this.scrollBarPadding.left - this.scrollBarBackgroundBorder;
                int scrollBarAreaWidth = this.scrollBarBackgroundBorder + this.scrollBarPadding.left + this.scrollBarWidth + this.scrollBarPadding.right + this.scrollBarBackgroundBorder;
                int scrollBarAreaHeight = this.getHeight();
                if(this.scrollBarStyle == ScrollBarStyle.MERGED)
                    scrollBarAreaHeight -= this.itemContainerPadding.top +  this.itemContainerPadding.bottom + this.listBackgroundBorder * 2;
                graphics.blitSprite(this.scrollBarBackground, scrollBarLeft, scrollBarTop, scrollBarAreaWidth, scrollBarAreaHeight);
                RenderSystem.disableBlend();
            }

            // Draw scroll bar
            boolean scrollBarEnabled = maxScroll > 0;
            int scrollBarStart = this.getScrollbarPosition();
            int scrollBarEnd = scrollBarStart + this.scrollBarWidth;
            int scrollBarHeight = this.getScrollbarHeight();
            int scrollBarTop = (int) (this.getScrollAreaTop() + (this.getScrollAreaHeight() - this.getScrollbarHeight()) * (this.getScrollAmount() / Math.max(maxScroll, 1)));
            boolean scrollBarHovered = ClientUtils.isPointInArea(mouseX, mouseY, scrollBarStart, scrollBarTop, this.scrollBarWidth, scrollBarHeight);
            if(this.scrollBarSprites != null)
            {
                ResourceLocation sprite = this.scrollBarSprites.getSprite(scrollBarEnabled, scrollBarHovered, this.scrolling);
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
        graphics.enableScissor(this.getRowLeft(), this.getY() + this.listBackgroundBorder, this.getRowRight(), this.getY() + this.getHeight() - this.listBackgroundBorder);

        int rowLeft = this.getRowLeft();
        int rowWidth = this.getRowWidth();
        int rowHeight = this.itemHeight;
        int rowCount = this.getItemCount();

        // For efficiency, find the index to start drawing based on scroll amount
        int startIndex = Math.max(0, (int) ((this.getScrollAmount() - this.itemContainerPadding.top) / (rowHeight + this.itemSpacing))); // TODO test
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
        int contentLeft = this.getX() + this.listBackgroundBorder + this.itemContainerPadding.left;
        int contentTop = this.getY() + this.listBackgroundBorder;
        int contentWidth = this.getRowWidth();
        int contentHeight = this.getHeight() - this.listBackgroundBorder * 2;
        if(ClientUtils.isPointInArea((int) mouseX, (int) mouseY, contentLeft, contentTop, contentWidth, contentHeight))
        {
            int rowLeft = this.getRowLeft();
            int rowWidth = this.getRowWidth();
            int rowHeight = this.itemHeight;
            int rowCount = this.getItemCount();
            int startIndex = Math.max(0, (int) ((this.getScrollAmount() - this.itemContainerPadding.top) / (rowHeight + this.itemSpacing)));
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
        this.scrolling = this.getMaxScroll() > 0 && button == GLFW.GLFW_MOUSE_BUTTON_LEFT && ClientUtils.isPointInArea((int) mouseX, (int) mouseY, this.getScrollbarPosition(), this.getScrollAreaTop(), this.scrollBarWidth, this.getScrollAreaHeight());
        ClientServices.CLIENT.setScrollingState(this, this.scrolling);
    }

    public static abstract class Item extends ObjectSelectionList.Entry<Item>
    {
        private boolean hovered;

        protected abstract void renderContent(GuiGraphics graphics, int index, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, boolean selected, float partialTick);

        private void setHovered(boolean hovered)
        {
            this.hovered = hovered;
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

    protected record Padding(int left, int top, int right, int bottom)
    {
        public Padding(int padding)
        {
            this(padding, padding, padding, padding);
        }
    }
}
