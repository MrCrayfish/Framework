package test.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;

@EventBusSubscriber(modid = "widgets_test", value = Dist.CLIENT)
public class WidgetsTestClient
{
    @SubscribeEvent
    public static void onRegisterCommands(RegisterClientCommandsEvent event)
    {
        event.getDispatcher().register(Commands.literal("widgets_test:open").executes(context -> {
            // changed: Minecraft#setScreen removed in MC 26.2, use setScreenAndShow instead
            Minecraft.getInstance().setScreenAndShow(new TestScreen());
            return 1;
        }));
    }
}
