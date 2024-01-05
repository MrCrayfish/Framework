package test.network;

import com.mrcrayfish.framework.Constants;
import com.mrcrayfish.framework.api.FrameworkAPI;
import com.mrcrayfish.framework.api.network.FrameworkNetwork;
import com.mrcrayfish.framework.api.network.FrameworkResponse;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.ConfigurationMessage;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.network.FriendlyByteBuf;
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
    public static FrameworkNetwork testHandshakeChannel;

    @Override
    public void onInitialize()
    {
        testPlayChannel = FrameworkAPI
                .createNetworkBuilder(new ResourceLocation("network_test", "play"), 1)
                .registerPlayMessage(TestMessage.class)
                .ignoreClient()
                .ignoreServer()
                .build();

        testHandshakeChannel = FrameworkAPI
                .createNetworkBuilder(new ResourceLocation("network_test", "handshake"), 1)
                .registerConfigurationMessage(TestHandshake.class, "test", () -> List.of(new TestHandshake()))
                .build();

        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if(!world.isClientSide()) {
                testPlayChannel.sendToPlayer(() -> (ServerPlayer) player, new TestMessage());
            }
            return InteractionResult.PASS;
        });
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
            Constants.LOG.info(MARKER, "Received test play message on side: " + context.getNetworkManager().getReceiving().name());
            context.setHandled(true);
        }
    }

    public static class TestHandshake extends ConfigurationMessage<TestHandshake>
    {
        @Override
        public void encode(TestHandshake message, FriendlyByteBuf buffer) {}

        @Override
        public TestHandshake decode(FriendlyByteBuf buffer)
        {
            return new TestHandshake();
        }

        @Override
        public FrameworkResponse handle(TestHandshake message, Consumer<Runnable> executor)
        {
            Constants.LOG.debug(MARKER, "Received test handshake message!");
            return FrameworkResponse.success();
        }
    }
}
