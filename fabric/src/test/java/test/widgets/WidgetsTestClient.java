package test.widgets;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class WidgetsTestClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if(player.getItemInHand(hand).is(Items.STICK)) {
                Minecraft.getInstance().setScreen(new TestScreen());
            }
            return InteractionResultHolder.fail(ItemStack.EMPTY);
        });
    }
}
