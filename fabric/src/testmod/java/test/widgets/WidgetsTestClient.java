package test.widgets;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Unit;

public class WidgetsTestClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(ClientCommandManager.literal("widgets_test:open").executes(context -> {
                // Scheduled due to how Fabric handles client commands
                Minecraft.getInstance().scheduleWithResult(completableFuture -> {
                    Minecraft.getInstance().setScreen(new TestScreen());
                    completableFuture.complete(Unit.INSTANCE);
                });
                return 1;
            }));
        });
    }
}
