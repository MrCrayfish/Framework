package test.widgets;

import com.mrcrayfish.framework.api.client.screen.Anchor;
import com.mrcrayfish.framework.api.client.screen.FrameworkScreen;
import com.mrcrayfish.framework.api.client.screen.overlay.impl.Modal;
import com.mrcrayfish.framework.api.client.screen.widget.*;
import com.mrcrayfish.framework.api.client.screen.widget.element.Icon;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;

public class TestScreen extends FrameworkScreen
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
        first.addChild(FrameworkSelect.builder(Identifier.class, rl -> Component.literal(rl.toString()))
            .setSize(100, 20)
            .setPreferredAnchor(Anchor.BELOW_LEFT)
            .setDropdownMinWidth(200)
            .setSearchable(true)
            .setValues(() -> BuiltInRegistries.PARTICLE_TYPE.keySet().stream().sorted(Comparator.comparing(Identifier::toString)).toList())
            .build());
        first.addChild(FrameworkButton.builder()
                .setLabel(Component.literal("Normal"))
            .setSize(100, 20)
            .setAction(btn -> {
                LinearLayout content = LinearLayout.vertical().spacing(4);
                content.addChild(FrameworkEditBox.builder()
                    .setSize(150, 20)
                    .setHint(Component.literal("Search..."))
                    .build());
                content.addChild(FrameworkSelectionList.builder()
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
                Modal.builder().setContent(content).build().show(Modal.Position.TOP);
            }).build());
        MutableBoolean state = new MutableBoolean();
        first.addChild(Buttons.createOnOff(Component.literal("Toggle"), state::getValue, state::setValue)
            .setSize(100, 20)
            .build());
        MutableBoolean iconState = new MutableBoolean();
        first.addChild(Buttons.createOnOff(Component.literal("Icon"), iconState::getValue, iconState::setValue)
            .setSize(100, 20)
            .setIcon(Icon.sprite(Identifier.withDefaultNamespace("icon/checkmark"), 9, 8))
            .setSpacing(5)
            .build());
        first.addChild(FrameworkButton.builder()
            .setSize(100, 20)
            .setLabel(Component.literal("Disabled"))
            .setIcon(Icon.sprite(Identifier.withDefaultNamespace("icon/checkmark"), 9, 8))
            .build()).active = false;
        first.addChild(FrameworkButton.builder()
            .setSize(100, 20)
            .setLabel(Component.literal("Tooltip"))
            .setTooltip(btn -> Tooltip.create(Component.literal("It is wednesday my dudes")))
            .setTooltipOptions(TooltipOptions.REBUILD_TOOLTIP_ON_WIDGET_HOVER)
            .build());
        first.addChild(FrameworkEditBox.builder()
            .setSize(100, 20)
            .setIcon(Icon.sprite(Identifier.withDefaultNamespace("icon/checkmark"), 9, 8))
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

        LinearLayout third = LinearLayout.vertical().spacing(4);
        third.addChild(FrameworkStepper.builder(FrameworkStepper.INT)
            .setSize(100, 20)
            .setMinValue(0)
            .setMaxValue(10)
            .setSpacing(2)
            .build());
        third.addChild(FrameworkStepper.builder(FrameworkStepper.LONG)
            .setSize(100, 20)
            .setMinValue(-100L)
            .setMaxValue(100L)
            .setBigStep(50L)
            .setSpacing(2)
            .build());
        third.addChild(FrameworkStepper.builder(FrameworkStepper.FLOAT)
            .setSize(100, 20)
            .setMinValue(-5.5F)
            .setMaxValue(10.125F)
            .setStep(2.5F)
            .setBigStep(3.75F)
            .setSpacing(2)
            .build());
        third.addChild(FrameworkStepper.builder(FrameworkStepper.DOUBLE)
            .setSize(100, 20)
            .setMinValue(-5.5)
            .setMaxValue(10.125)
            .setSpacing(2)
            .build());
        wrapper.addChild(third);

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
        protected void renderContent(GuiGraphics graphics, int mouseX, int mouseY, boolean hovered, boolean selected, float partialTick)
        {
            int textY = this.getContentY() + (this.getContentHeight() - 8) / 2;
            graphics.drawString(Minecraft.getInstance().font, this.text, this.getContentX() + 5, textY, 0xFFFFFFFF);
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
        protected void renderContent(GuiGraphics graphics, int mouseX, int mouseY, boolean hovered, boolean selected, float partialTick)
        {
            int textY = this.getContentY() + (this.getContentHeight() - 8) / 2;
            graphics.drawCenteredString(Minecraft.getInstance().font, this.text, this.getContentX() + this.getContentWidth() / 2, textY, 0xFFFFFFFF);
        }

        @Override
        protected void renderBackground(FrameworkSelectionList.@Nullable ItemSprites sprites, GuiGraphics graphics, int mouseX, int mouseY, boolean hovered, boolean selected) {}
    }
}
