package test.network;

import com.mrcrayfish.framework.Constants;
import com.mrcrayfish.framework.FrameworkSetup;
import com.mrcrayfish.framework.api.FrameworkAPI;
import com.mrcrayfish.framework.api.network.ConfigurationMessageContext;
import com.mrcrayfish.framework.api.network.FrameworkNetwork;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.registry.RegistryContainer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.List;

@RegistryContainer
public class NetworkTest implements ModInitializer
{
    public static final Marker MARKER = MarkerFactory.getMarker("NETWORK_TEST");

    public static final FrameworkNetwork TEST_PLAY_CHANNEL = FrameworkAPI
            .createNetworkBuilder(ResourceLocation.fromNamespaceAndPath("network_test", "play"), 1)
            .registerPlayMessage("test", TestMessage.class, TestMessage.STREAM_CODEC, TestMessage::handle, PacketFlow.CLIENTBOUND)
            .optional()
            .build();

    public static final FrameworkNetwork TEST_CONFIGURATION_CHANNEL = FrameworkAPI
            .createNetworkBuilder(ResourceLocation.fromNamespaceAndPath("network_test", "configuration"), 1)
            .registerConfigurationMessage("test", TestConfiguration.class, TestConfiguration.STREAM_CODEC, TestConfiguration::handle, () -> List.of(new TestConfiguration()))
            .build();

    public NetworkTest()
    {
        FrameworkSetup.run();
    }

    @Override
    public void onInitialize()
    {
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if(!world.isClientSide()) {
                TEST_PLAY_CHANNEL.sendToPlayer(() -> (ServerPlayer) player, new TestMessage());
            }
            return InteractionResult.PASS;
        });
    }

    public record TestMessage()
    {
        private static final TestMessage INSTANCE = new TestMessage();
        public static final StreamCodec<RegistryFriendlyByteBuf, TestMessage> STREAM_CODEC = StreamCodec.unit(INSTANCE);

        public static void handle(TestMessage message, MessageContext context)
        {
            Constants.LOG.info(MARKER, "Received test play message on flow: " + context.getFlow());
            context.setHandled(true);
        }
    }

    public record TestConfiguration()
    {
        private static final TestConfiguration INSTANCE = new TestConfiguration();
        public static final StreamCodec<FriendlyByteBuf, TestConfiguration> STREAM_CODEC = StreamCodec.unit(INSTANCE);

        public static void handle(TestConfiguration message, ConfigurationMessageContext context)
        {
            Constants.LOG.debug(MARKER, "Received test configuration message!");
            context.setHandled(true);
        }
    }
}
