package test.widgets;

import com.mrcrayfish.framework.api.client.screen.widget.Buttons;
import com.mrcrayfish.framework.api.client.screen.widget.TooltipOptions;
import com.mrcrayfish.framework.api.client.screen.widget.FrameworkButton;
import com.mrcrayfish.framework.api.client.screen.widget.FrameworkEditBox;
import com.mrcrayfish.framework.api.client.screen.widget.FrameworkSelectionList;
import com.mrcrayfish.framework.api.client.screen.widget.element.Icon;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
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

        LinearLayout wrapper = LinearLayout.horizontal().spacing(4);

        LinearLayout first = LinearLayout.vertical().spacing(2);
        first.addChild(FrameworkButton.builder()
            .setSize(100, 20)
            .setLabel(Component.literal("Normal"))
            .build());
        MutableBoolean state = new MutableBoolean();
        first.addChild(Buttons.createOnOff(Component.literal("Toggle"), state::getValue, state::setValue)
            .setSize(100, 20)
            .build());
        MutableBoolean iconState = new MutableBoolean();
        first.addChild(Buttons.createOnOff(Component.literal("Icon"), iconState::getValue, iconState::setValue)
            .setSize(100, 20)
            .setIcon(Icon.sprite(ResourceLocation.withDefaultNamespace("icon/checkmark"), 9, 8))
            .setSpacing(5)
            .build());
        first.addChild(FrameworkButton.builder()
            .setSize(100, 20)
            .setLabel(Component.literal("Disabled"))
            .setIcon(Icon.sprite(ResourceLocation.withDefaultNamespace("icon/checkmark"), 9, 8))
            .build()).active = false;
        first.addChild(FrameworkButton.builder()
            .setSize(100, 20)
            .setLabel(Component.literal("Tooltip"))
            .setTooltip(btn -> Tooltip.create(Component.literal("It is wednesday my dudes")))
            .setTooltipOptions(TooltipOptions.REBUILD_TOOLTIP_ON_WIDGET_HOVER)
            .build());
        first.addChild(FrameworkEditBox.builder()
            .setSize(100, 40)
            .setInitialText("Stone Axe")
            .build());
        first.addChild(FrameworkEditBox.builder()
            .setSize(100, 20)
            .setIcon(Icon.sprite(ResourceLocation.withDefaultNamespace("icon/checkmark"), 9, 8))
            .setInitialText("Stone Axe")
            .build());
        MutableBoolean state2 = new MutableBoolean();
        first.addChild(Buttons.createToggle(Component.literal("Test"), state2::getValue, state2::setValue)
            .setSize(100, 20)
            .build());
        wrapper.addChild(first);

        LinearLayout second = LinearLayout.vertical().spacing(4);
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
            }).build());

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
            }).build());

        wrapper.addChild(second);

        wrapper.arrangeElements();
        wrapper.setPosition(10, 10);
        wrapper.visitWidgets(this::addRenderableWidget);
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
