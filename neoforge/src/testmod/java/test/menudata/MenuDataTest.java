package test.menudata;

import com.mrcrayfish.framework.api.FrameworkAPI;
import com.mrcrayfish.framework.api.menu.IMenuData;
import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
import net.minecraft.commands.Commands;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/**
 * Author: MrCrayfish
 */
@Mod("menu_data_test")
@RegistryContainer
public class MenuDataTest
{
    public static final RegistryEntry<MenuType<TestMenu>> TEST_MENU = RegistryEntry.menuTypeWithData(ResourceLocation.fromNamespaceAndPath("menu_data_test", "test_menu"), TestMenu.CustomData.STREAM_CODEC, TestMenu::new);

    public MenuDataTest()
    {
        NeoForge.EVENT_BUS.addListener(this::onRegisterCommands);
    }

    private void onRegisterCommands(RegisterCommandsEvent event)
    {
        event.getDispatcher().register(Commands.literal("data_menu_test:open").executes(context -> {
            if(context.getSource().source instanceof ServerPlayer player) {
                FrameworkAPI.openMenuWithData(player, new SimpleMenuProvider((windowId, playerInventory, player1) -> {
                    return new TestMenu(windowId, playerInventory, 1, "Test");
                }, Component.literal("Hello")), new TestMenu.CustomData(5, "Hello from the server!"));
            }
            return 1;
        }));
    }

    public static class TestMenu extends AbstractContainerMenu
    {
        private final int count;
        private final String message;

        private TestMenu(int windowId, Inventory playerInventory, CustomData data)
        {
            this(windowId, playerInventory, data.count(), data.message());
            System.out.println(this.count);
            System.out.println(this.message);
        }

        private TestMenu(int windowId, Inventory playerInventory, int count, String message)
        {
            super(TEST_MENU.get(), windowId);
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

        public record CustomData(int count, String message) implements IMenuData<CustomData>
        {
            public static final StreamCodec<RegistryFriendlyByteBuf, CustomData> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT,
                CustomData::count,
                ByteBufCodecs.STRING_UTF8,
                CustomData::message,
                CustomData::new
            );

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, CustomData> codec()
            {
                return STREAM_CODEC;
            }
        }
    }
}
