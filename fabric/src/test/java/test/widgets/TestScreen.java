package test.widgets;

import com.mrcrayfish.framework.api.client.screen.widget.*;
import com.mrcrayfish.framework.api.client.screen.widget.element.Icon;
import com.mrcrayfish.framework.api.client.screen.widget.texture.FrameworkTexture;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nullable;

public class TestScreen extends Screen
{
    protected TestScreen()
    {
        super(CommonComponents.EMPTY);
    }

    @Override
    protected void init()
    {
        super.init();

        GridLayout wrapper = new GridLayout().spacing(4);

        GridLayout test = new GridLayout().spacing(4);
        test.addChild(FrameworkButton.builder()
                .setSize(100, 20)
                .setLabel(Component.literal("Normal"))
                .build(), 0, 0);
        MutableBoolean state = new MutableBoolean();
        test.addChild(Buttons.createOnOff(Component.literal("Toggle"), state::getValue, state::setValue)
                .setSize(100, 20)
                .build(), 1, 0);
        MutableBoolean iconState = new MutableBoolean();
        test.addChild(Buttons.createOnOff(Component.literal("Icon"), iconState::getValue, iconState::setValue)
                .setSize(100, 20)
                .setIcon(FrameworkTexture.subImage(new ResourceLocation("textures/gui/checkmark.png"), 0, 0, 9, 8, 9, 8))
                .setSpacing(5)
                .build(), 2, 0);
        test.addChild(FrameworkButton.builder()
                .setSize(100, 20)
                .setLabel(Component.literal("Disabled"))
                .setIcon(FrameworkTexture.subImage(new ResourceLocation("textures/gui/checkmark.png"), 0, 0, 9, 8, 9, 8))
                .build(), 3, 0).active = false;
        test.addChild(FrameworkButton.builder()
                .setSize(100, 20)
                .setLabel(Component.literal("Tooltip"))
                .setTooltip(btn -> Tooltip.create(Component.literal("It is wednesday my dudes")))
                .setTooltipOptions(TooltipOptions.REBUILD_TOOLTIP_ON_WIDGET_HOVER)
                .build(), 4, 0);
        test.addChild(FrameworkEditBox.builder()
                .setSize(100, 40)
                .setInitialText("Stone Axe")
                .build(), 5, 0);
        test.addChild(FrameworkEditBox.builder()
                .setSize(100, 20)
                .setIcon(FrameworkTexture.subImage(new ResourceLocation("textures/gui/checkmark.png"), 0, 0, 9, 8, 9, 8))
                .setInitialText("Stone Axe")
                .build(), 6, 0);
        MutableBoolean state2 = new MutableBoolean();
        test.addChild(Buttons.createToggle(Component.literal("Test"), state2::getValue, state2::setValue)
                .setSize(100, 20)
                .build(), 7, 0);
        wrapper.addChild(test, 0, 0);

        GridLayout second = new GridLayout().spacing(4);
        second.addChild(FrameworkSelectionList.builder()
            .setSize(150, 100)
            .setItemHeight(16)
            .noListBackground()
            .setScrollBarStyle(FrameworkSelectionList.ScrollBarStyle.DETACHED)
            .setScrollBarAlwaysVisible(true)
            .setInitialItems(items -> {
                items.accept(new TextItem("Apple"));
                items.accept(new TextItem("Banana"));
                items.accept(new TextItem("Orange"));
                items.accept(new TextItem("Mango"));
                items.accept(new TextItem("Apple"));
                items.accept(new TextItem("Banana"));
                items.accept(new TextItem("Orange"));
                items.accept(new TextItem("Mango"));
                items.accept(new TextItem("Apple"));
                items.accept(new TextItem("Banana"));
                items.accept(new TextItem("Orange"));
                items.accept(new TextItem("Mango"));
            }).build(), 0, 0);

        second.addChild(FrameworkSelectionList.builder()
            .setSize(150, 100)
            .setItemHeight(16)
            .setScrollBarStyle(FrameworkSelectionList.ScrollBarStyle.MERGED)
            .setScrollBarAlwaysVisible(true)
            .setInitialItems(items -> {
                items.accept(new TitleItem(Component.literal("Fruits")));
                items.accept(new TextItem("Apple"));
                items.accept(new TextItem("Banana"));
                items.accept(new TextItem("Orange"));
                items.accept(new TextItem("Mango"));
                items.accept(new TextItem("Apple"));
                items.accept(new TextItem("Banana"));
                items.accept(new TextItem("Orange"));
                items.accept(new TextItem("Mango"));
                items.accept(new TextItem("Apple"));
                items.accept(new TextItem("Banana"));
                items.accept(new TextItem("Orange"));
                items.accept(new TextItem("Mango"));
            }).build(), 1, 0);
        wrapper.addChild(second, 0, 1);

        wrapper.arrangeElements();
        wrapper.setPosition(5, 5);
        wrapper.visitWidgets(this::addRenderableWidget);

        second.visitChildren(element -> {
            if(element instanceof FrameworkSelectionList) {
                this.addRenderableWidget((FrameworkSelectionList) element);
            }
        });
    }

    private static class TextItem extends FrameworkSelectionList.Item
    {
        private final String text;

        public TextItem(String text)
        {
            this.text = text;
        }

        @Override
        public void renderContent(GuiGraphics graphics, int index, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, boolean selected, float partialTick)
        {
            int textY = y + (height - 8) / 2;
            graphics.drawString(Minecraft.getInstance().font, this.text, x + 5, textY, 0xFFFFFFFF);
        }
    }

    private static class TitleItem extends FrameworkSelectionList.Item
    {
        private final Component text;

        public TitleItem(Component text)
        {
            this.text = text.plainCopy().withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD);
        }

        @Override
        public boolean isSelectable()
        {
            return false;
        }

        @Override
        public void renderContent(GuiGraphics graphics, int index, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, boolean selected, float partialTick)
        {
            int textY = y + (height - 8) / 2;
            graphics.drawCenteredString(Minecraft.getInstance().font, this.text, x + width / 2, textY, 0xFFFFFFFF);
        }

        @Override
        protected void renderBackground(@Nullable FrameworkSelectionList.ItemSprites sprites, GuiGraphics graphics, int index, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, boolean selected) {}
    }
}
