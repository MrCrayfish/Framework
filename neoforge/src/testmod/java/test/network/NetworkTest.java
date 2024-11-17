package test.network;

import com.mrcrayfish.framework.Constants;
import com.mrcrayfish.framework.api.FrameworkAPI;
import com.mrcrayfish.framework.api.network.FrameworkNetwork;
import com.mrcrayfish.framework.api.network.FrameworkResponse;
import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
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

    public NetworkTest(IEventBus bus)
    {
        bus.addListener(this::onCommonSetup);
        NeoForge.EVENT_BUS.addListener(this::onRightClickBlock);
    }

    private void onCommonSetup(FMLCommonSetupEvent event)
    {
        testPlayChannel = FrameworkAPI
            .createNetworkBuilder(ResourceLocation.fromNamespaceAndPath("network_test", "play"), 1)
            .registerPlayMessage("test", TestMessage.class, TestMessage.STREAM_CODEC, TestMessage::handle)
            .optional()
            .build();

        testConfigurationChannel = FrameworkAPI
            .createNetworkBuilder(ResourceLocation.fromNamespaceAndPath("network_test", "configuration"), 1)
            .registerConfigurationMessage("test", TestConfiguration.class, TestConfiguration.STREAM_CODEC, TestConfiguration::handle, () -> List.of(new TestConfiguration()))
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
        private static final TestMessage INSTANCE = new TestMessage();
        public static final StreamCodec<RegistryFriendlyByteBuf, TestMessage> STREAM_CODEC = StreamCodec.unit(INSTANCE);

        public static void handle(TestMessage message, MessageContext context)
        {
            Constants.LOG.info(MARKER, "Received test play message on side: " + context.getFlow().name());
            context.setHandled(true);
        }
    }

    public record TestConfiguration()
    {
        private static final TestConfiguration INSTANCE = new TestConfiguration();
        public static final StreamCodec<FriendlyByteBuf, TestConfiguration> STREAM_CODEC = StreamCodec.unit(INSTANCE);

        public static FrameworkResponse handle(TestConfiguration message, Consumer<Runnable> executor)
        {
            Constants.LOG.debug(MARKER, "Received test configuration message!");
            return FrameworkResponse.SUCCESS;
        }
    }
}
