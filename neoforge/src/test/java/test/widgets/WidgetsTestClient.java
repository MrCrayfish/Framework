package test.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;

@EventBusSubscriber(modid = "widgets_test", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public class WidgetsTestClient
{
    @SubscribeEvent
    public static void onRegisterCommands(RegisterClientCommandsEvent event)
    {
        event.getDispatcher().register(Commands.literal("widgets_test:open").executes(context -> {
            Minecraft.getInstance().setScreen(new TestScreen());
            return 1;
        }));
    }
}
