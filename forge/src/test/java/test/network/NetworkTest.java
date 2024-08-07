package test.network;

import com.mrcrayfish.framework.FrameworkForge;
import com.mrcrayfish.framework.api.FrameworkAPI;
import com.mrcrayfish.framework.api.network.FrameworkNetwork;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.HandshakeMessage;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

/**
 * Author: MrCrayfish
 */
@Mod("network_test")
public class NetworkTest
{
    public static final Marker MARKER = MarkerManager.getMarker("NETWORK_TEST");

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

    public NetworkTest()
    {
        MinecraftForge.EVENT_BUS.addListener(this::onRightClickBlock);
    }

    private void onRightClickBlock(PlayerInteractEvent.RightClickBlock event)
    {
        if(event.getSide() != LogicalSide.SERVER)
            return;

        Player player = event.getEntity();
        if(!(player instanceof ServerPlayer))
            return;

        TEST_PLAY_CHANNEL.sendToPlayer(() -> (ServerPlayer) player, new TestMessage());
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
            FrameworkForge.LOGGER.debug(MARKER, "Received test handshake message!");
            context.setHandled(true);
            context.reply(new Acknowledge());
        }
    }
}
