package test.network;

import com.mrcrayfish.framework.Constants;
import com.mrcrayfish.framework.api.FrameworkAPI;
import com.mrcrayfish.framework.api.network.FrameworkNetwork;
import com.mrcrayfish.framework.api.network.FrameworkResponse;
import com.mrcrayfish.framework.api.network.MessageContext;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.List;
import java.util.function.Consumer;

public class NetworkTest implements ModInitializer
{
    public static final Marker MARKER = MarkerFactory.getMarker("NETWORK_TEST");

    public static FrameworkNetwork testPlayChannel;
    public static FrameworkNetwork testConfigurationChannel;

    @Override
    public void onInitialize()
    {
        testPlayChannel = FrameworkAPI
                .createNetworkBuilder(new ResourceLocation("network_test", "play"), 1)
                .registerPlayMessage("test", TestMessage.class, TestMessage.STREAM_CODEC, TestMessage::handle)
                .optional()
                .build();

        testConfigurationChannel = FrameworkAPI
                .createNetworkBuilder(new ResourceLocation("network_test", "configuration"), 1)
                .registerConfigurationMessage("test", TestConfiguration.class, TestConfiguration.STREAM_CODEC, TestConfiguration::handle, () -> List.of(new TestConfiguration()))
                .build();

        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if(!world.isClientSide()) {
                testPlayChannel.sendToPlayer(() -> (ServerPlayer) player, new TestMessage());
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

        public static FrameworkResponse handle(TestConfiguration message, Consumer<Runnable> executor)
        {
            Constants.LOG.debug(MARKER, "Received test configuration message!");
            return FrameworkResponse.success();
        }
    }
}
