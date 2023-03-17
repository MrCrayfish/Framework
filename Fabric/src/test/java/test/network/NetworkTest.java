package test.network;

import com.mrcrayfish.framework.Constants;
import com.mrcrayfish.framework.api.FrameworkAPI;
import com.mrcrayfish.framework.api.network.FrameworkNetwork;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.HandshakeMessage;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class NetworkTest implements ModInitializer
{
    public static final Marker MARKER = MarkerFactory.getMarker("NETWORK_TEST");

    public static final FrameworkNetwork TEST_PLAY_CHANNEL = FrameworkAPI
            .createNetworkBuilder(new ResourceLocation("network_test", "play"), 1)
            .registerPlayMessage(TestMessage.class)
            .ignoreClient()
            .ignoreServer()
            .build();

    public static final FrameworkNetwork TEST_HANDSHAKE_CHANNEL = FrameworkAPI
            .createNetworkBuilder(new ResourceLocation("network_test", "handshake"), 1)
            .registerHandshakeMessage(TestHandshake.class, true)
            .build();

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

    public static class TestHandshake extends HandshakeMessage<TestHandshake>
    {
        @Override
        public void encode(TestHandshake message, FriendlyByteBuf buffer) {}

        @Override
        public TestHandshake decode(FriendlyByteBuf buffer)
        {
            return new TestHandshake();
        }

        @Override
        public void handle(TestHandshake message, MessageContext context)
        {
            Constants.LOG.debug(MARKER, "Received test handshake message!");
            context.setHandled(true);
            context.reply(new Acknowledge());
        }
    }
}
