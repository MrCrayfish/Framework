package test.widgets;

import com.mrcrayfish.framework.api.client.screen.TooltipOptions;
import com.mrcrayfish.framework.api.client.screen.widget.FrameworkButton;
import com.mrcrayfish.framework.api.client.screen.widget.element.Icon;
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

        LinearLayout layout = LinearLayout.vertical().spacing(2);
        layout.addChild(FrameworkButton.builder()
            .setSize(100, 20)
            .setLabel(Component.literal("Normal"))
            .build());
        MutableBoolean state = new MutableBoolean();
        layout.addChild(FrameworkButton.state(state::getValue, state::setValue)
            .setSize(100, 20)
            .setLabel(() -> CommonComponents.optionStatus(Component.literal("Toggle"), state.getValue()))
            .build());
        MutableBoolean iconState = new MutableBoolean();
        layout.addChild(FrameworkButton.state(iconState::getValue, iconState::setValue)
            .setSize(100, 20)
            .setLabel(() -> CommonComponents.optionStatus(Component.literal("Icon"), iconState.getValue()))
            .setIcon(Icon.sprite(ResourceLocation.withDefaultNamespace("icon/checkmark"), 9, 8))
            .setSpacing(5)
            .build());
        layout.addChild(FrameworkButton.builder()
            .setSize(100, 20)
            .setLabel(Component.literal("Disabled"))
            .setIcon(Icon.sprite(ResourceLocation.withDefaultNamespace("icon/checkmark"), 9, 8))
            .build()).active = false;
        layout.addChild(FrameworkButton.builder()
            .setSize(100, 20)
            .setLabel(Component.literal("Tooltip"))
            .setTooltip(btn -> Tooltip.create(Component.literal("It is wednesday my dudes")))
            .setTooltipOptions(TooltipOptions.REBUILD_TOOLTIP_ON_WIDGET_HOVER)
            .build());
        layout.arrangeElements();
        layout.setPosition(10, 10);
        layout.visitWidgets(this::addRenderableWidget);
    }
}
