package test.menudata;

import com.mrcrayfish.framework.api.FrameworkAPI;
import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.commands.Commands;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Author: MrCrayfish
 */
@Mod("menu_data_test")
@RegistryContainer
public class MenuDataTest
{
    public static final RegistryEntry<MenuType<TestMenu>> TEST_MENU = RegistryEntry.menuTypeWithData(new ResourceLocation("data_loader_test", "test_menu"), TestMenu::new);

    public MenuDataTest()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            bus.addListener(this::onClientSetup);
        });
        MinecraftForge.EVENT_BUS.addListener(this::onRegisterCommands);
    }

    private void onClientSetup(FMLClientSetupEvent event)
    {
        event.enqueueWork(() -> MenuScreens.register(TEST_MENU.get(), TestScreen::new));
    }

    private void onRegisterCommands(RegisterCommandsEvent event)
    {
        event.getDispatcher().register(Commands.literal("data_menu_test:open").executes(context -> {
            if(context.getSource().source instanceof ServerPlayer player) {
                FrameworkAPI.openMenuWithData(player, new SimpleMenuProvider((windowId, playerInventory, player1) -> {
                    return new TestMenu(windowId, playerInventory, 1, "Test");
                }, Component.literal("Hello")), buffer -> {
                    buffer.writeInt(5);
                    buffer.writeUtf("Hello from the server!");
                });
            }
            return 1;
        }));
    }

    public static class TestScreen extends AbstractContainerScreen<TestMenu>
    {
        public TestScreen(TestMenu menu, Inventory playerInventory, Component title)
        {
            super(menu, playerInventory, title);
        }

        @Override
        protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY)
        {
            this.renderBackground(graphics, mouseX, mouseY, partialTick);
            graphics.drawString(this.font, Integer.toString(this.menu.getCount()), 0, 0, 0xFFFFFF);
            graphics.drawString(this.font, this.menu.getMessage(), 0, 20, 0xFFFFFF);
        }
    }

    public static class TestMenu extends AbstractContainerMenu
    {
        private final int count;
        private final String message;

        private TestMenu(int windowId, Inventory playerInventory, FriendlyByteBuf buffer)
        {
            this(windowId, playerInventory, buffer.readInt(), buffer.readUtf());
            System.out.println(this.count);
            System.out.println(this.message);
        }

        private TestMenu(int p_38852_, Inventory playerInventory, int count, String message)
        {
            super(TEST_MENU.get(), p_38852_);
            this.count = count;
            this.message = message;
        }

        @Override
        public ItemStack quickMoveStack(Player player, int slotIndex)
        {
            return ItemStack.EMPTY;
        }

        @Override
        public boolean stillValid(Player player)
        {
            return true;
        }

        public int getCount()
        {
            return this.count;
        }

        public String getMessage()
        {
            return this.message;
        }
    }
}
