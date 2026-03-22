package test.menudata;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/**
 * Author: MrCrayfish
 */
@EventBusSubscriber(modid = "menu_data_test", value = Dist.CLIENT)
public class MenuDataTestClient
{
    @SubscribeEvent
    private static void onRegisterMenuScreens(RegisterMenuScreensEvent event)
    {
        event.register(MenuDataTest.TEST_MENU.get(), TestScreen::new);
    }

    public static class TestScreen extends AbstractContainerScreen<MenuDataTest.TestMenu>
    {
        public TestScreen(MenuDataTest.TestMenu menu, Inventory playerInventory, Component title)
        {
            super(menu, playerInventory, title);
        }

        @Override
        protected void extractMenuBackground(GuiGraphicsExtractor extractor, int x, int y, int width, int height)
        {
            super.extractMenuBackground(extractor, x, y, width, height);
            extractor.text(this.font, Integer.toString(this.menu.getCount()), 0, 0, 0xFFFFFFFF);
            extractor.text(this.font, this.menu.getMessage(), 0, 20, 0xFFFFFFFF);
        }
    }
}
