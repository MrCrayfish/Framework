package test.widgets;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Unit;

public class WidgetsTestClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(ClientCommands.literal("widgets_test:open").executes(context -> {
                // Scheduled due to how Fabric handles client commands
                Minecraft.getInstance().scheduleWithResult(completableFuture -> {
                    // changed: Minecraft#setScreen removed in MC 26.2, use setScreenAndShow instead
                    Minecraft.getInstance().setScreenAndShow(new TestScreen());
                    completableFuture.complete(Unit.INSTANCE);
                });
                return 1;
            }));
        });
    }
}
