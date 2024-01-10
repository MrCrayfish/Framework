package test.network;

import com.mrcrayfish.framework.Constants;
import com.mrcrayfish.framework.api.FrameworkAPI;
import com.mrcrayfish.framework.api.network.FrameworkNetwork;
import com.mrcrayfish.framework.api.network.FrameworkResponse;
import com.mrcrayfish.framework.api.network.MessageContext;
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
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.List;
import java.util.function.Consumer;

/**
 * Author: MrCrayfish
 */
@Mod("network_test")
public class NetworkTest
{
    public static final Marker MARKER = MarkerFactory.getMarker("NETWORK_TEST");

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
            .registerPlayMessage("test", TestMessage.class, TestMessage::encode, TestMessage::decode, TestMessage::handle)
            .optional()
            .build();

        testConfigurationChannel = FrameworkAPI
            .createNetworkBuilder(new ResourceLocation("network_test", "configuration"), 1)
            .registerConfigurationMessage("test", TestConfiguration.class, TestConfiguration::encode, TestConfiguration::decode, TestConfiguration::handle, () -> List.of(new TestConfiguration()))
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

    public record TestMessage()
    {
        public static void encode(TestMessage message, FriendlyByteBuf buffer) {}

        public static TestMessage decode(FriendlyByteBuf buffer)
        {
            return new TestMessage();
        }

        public static void handle(TestMessage message, MessageContext context)
        {
            Constants.LOG.info(MARKER, "Received test play message on side: " + context.getFlow().name());
            context.setHandled(true);
        }
    }

    public record TestConfiguration()
    {
        public static void encode(TestConfiguration message, FriendlyByteBuf buffer) {}

        public static TestConfiguration decode(FriendlyByteBuf buffer)
        {
            return new TestConfiguration();
        }

        public static FrameworkResponse handle(TestConfiguration message, Consumer<Runnable> executor)
        {
            Constants.LOG.debug(MARKER, "Received test configuration message!");
            return FrameworkResponse.SUCCESS;
        }
    }
}
