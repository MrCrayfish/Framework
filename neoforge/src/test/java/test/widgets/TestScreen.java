package test.widgets;

import com.mrcrayfish.framework.api.client.screen.Buttons;
import com.mrcrayfish.framework.api.client.screen.TooltipOptions;
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
        first.addChild(Buttons.createOnOffOption(Component.literal("Toggle"), state::getValue, state::setValue)
            .setSize(100, 20)
            .build());
        MutableBoolean iconState = new MutableBoolean();
        first.addChild(Buttons.createOnOffOption(Component.literal("Icon"), iconState::getValue, iconState::setValue)
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
        wrapper.addChild(first);

        LinearLayout second = LinearLayout.vertical().spacing(4);
        var list = new FrameworkSelectionList(150, 100, 0, 0, 16);
        list.setScrollBarStyle(FrameworkSelectionList.ScrollBarStyle.DETACHED);
        list.setScrollBarAlwaysVisible(true);
        list.addItem(new TextItem("Apple"));
        list.addItem(new TextItem("Banana"));
        list.addItem(new TextItem("Orange"));
        list.addItem(new TextItem("Mango"));
        list.addItem(new TextItem("Apple"));
        list.addItem(new TextItem("Banana"));
        list.addItem(new TextItem("Orange"));
        list.addItem(new TextItem("Mango"));
        list.addItem(new TextItem("Apple"));
        list.addItem(new TextItem("Banana"));
        list.addItem(new TextItem("Orange"));
        list.addItem(new TextItem("Mango"));
        second.addChild(list);

        var list2 = new FrameworkSelectionList(150, 100, 0, 0, 16);
        list2.setScrollBarStyle(FrameworkSelectionList.ScrollBarStyle.MERGED);
        list2.setScrollBarAlwaysVisible(true);
        list2.addItem(new TitleItem(Component.literal("Fruits")));
        list2.addItem(new TextItem("Apple"));
        list2.addItem(new TextItem("Banana"));
        list2.addItem(new TextItem("Orange"));
        list2.addItem(new TextItem("Mango"));
        list2.addItem(new TextItem("Apple"));
        list2.addItem(new TextItem("Banana"));
        list2.addItem(new TextItem("Orange"));
        list2.addItem(new TextItem("Mango"));
        list2.addItem(new TextItem("Apple"));
        list2.addItem(new TextItem("Banana"));
        list2.addItem(new TextItem("Orange"));
        list2.addItem(new TextItem("Mango"));
        second.addChild(list2);

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
    }
}
