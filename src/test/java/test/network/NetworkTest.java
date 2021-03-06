package test.network;

import com.mrcrayfish.framework.Framework;
import com.mrcrayfish.framework.api.network.FrameworkChannelBuilder;
import com.mrcrayfish.framework.api.network.HandshakeMessage;
import com.mrcrayfish.framework.api.network.PlayMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
@Mod("network_test")
public class NetworkTest
{
    public static final Marker MARKER = MarkerManager.getMarker("NETWORK_TEST");

    public static final SimpleChannel TEST_PLAY_CHANNEL = FrameworkChannelBuilder
            .create("network_test", "play", 1)
            .registerPlayMessage(TestMessage.class)
            .ignoreClient()
            .ignoreServer()
            .build();

    public static final SimpleChannel TEST_HANDSHAKE_CHANNEL = FrameworkChannelBuilder
            .create("network_test", "handshake", 1)
            .registerHandshakeMessage(TestHandshake.class)
            .build();

    public NetworkTest()
    {
        MinecraftForge.EVENT_BUS.addListener(this::onRightClickBlock);
    }

    private void onRightClickBlock(PlayerInteractEvent.RightClickBlock event)
    {
        if(event.getSide() != LogicalSide.SERVER)
            return;

        if(!(event.getEntityLiving() instanceof Player player))
            return;

        TEST_PLAY_CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new TestMessage());
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
        public void handle(TestMessage message, Supplier<NetworkEvent.Context> supplier)
        {
            Framework.LOGGER.info(MARKER, "Received test play message on side: " + supplier.get().getDirection().name());
            supplier.get().setPacketHandled(true);
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
        public void handle(TestHandshake message, Supplier<NetworkEvent.Context> supplier)
        {
            Framework.LOGGER.debug(MARKER, "Received test handshake message!");
            supplier.get().setPacketHandled(true);
            TEST_HANDSHAKE_CHANNEL.reply(new Acknowledge(), supplier.get());
        }
    }
}
