package test.network;

import com.mrcrayfish.framework.FrameworkForge;
import com.mrcrayfish.framework.api.FrameworkAPI;
import com.mrcrayfish.framework.api.network.FrameworkNetwork;
import com.mrcrayfish.framework.api.network.FrameworkResponse;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.ConfigurationMessage;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.List;
import java.util.function.Consumer;

/**
 * Author: MrCrayfish
 */
@Mod("network_test")
public class NetworkTest
{
    public static final Marker MARKER = MarkerManager.getMarker("NETWORK_TEST");

    public static FrameworkNetwork testPlayChannel;
    public static FrameworkNetwork testConfigurationChannel;

    public NetworkTest()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::onCommonSetup);
        MinecraftForge.EVENT_BUS.addListener(this::onRightClickBlock);
    }

    private void onCommonSetup(FMLCommonSetupEvent event)
    {
        testPlayChannel = FrameworkAPI
            .createNetworkBuilder(new ResourceLocation("network_test", "play"), 1)
            .registerPlayMessage(TestMessage.class)
            .ignoreClient()
            .ignoreServer()
            .build();

        testConfigurationChannel = FrameworkAPI
            .createNetworkBuilder(new ResourceLocation("network_test", "configuration"), 1)
            .registerConfigurationMessage(TestConfiguration.class, "test", () -> List.of(new TestConfiguration()))
            .build();
    }

    private void onRightClickBlock(PlayerInteractEvent.RightClickBlock event)
    {
        if(event.getSide() != LogicalSide.SERVER)
            return;

        Player player = event.getEntity();
        if(!(player instanceof ServerPlayer))
            return;

        testPlayChannel.sendToPlayer(() -> (ServerPlayer) player, new TestMessage());
    }

    public static class TestMessage extends PlayMessage<TestMessage>
    {
        @Override
        public void encode(TestMessage message, FriendlyByteBuf buffer) {}

        @Override
        public TestMessage decode(FriendlyByteBuf buffer)
        {
            return new TestMessage();
        }

        @Override
        public void handle(TestMessage message, MessageContext context)
        {
            FrameworkForge.LOGGER.info(MARKER, "Received test play message on side: " + context.getDirection().name());
            context.setHandled(true);
        }
    }

    public static class TestConfiguration extends ConfigurationMessage<TestConfiguration>
    {
        @Override
        public void encode(TestConfiguration message, FriendlyByteBuf buffer) {}

        @Override
        public TestConfiguration decode(FriendlyByteBuf buffer)
        {
            return new TestConfiguration();
        }

        @Override
        public FrameworkResponse handle(TestConfiguration message, Consumer<Runnable> executor)
        {
            FrameworkForge.LOGGER.debug(MARKER, "Received test configuration message!");
            return FrameworkResponse.SUCCESS;
        }
    }
}
