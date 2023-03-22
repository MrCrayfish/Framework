package com.mrcrayfish.framework.platform.network;

import com.mrcrayfish.framework.api.network.FrameworkNetwork;
import com.mrcrayfish.framework.network.message.IMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class ForgeNetwork implements FrameworkNetwork
{
    private final SimpleChannel channel;

    public ForgeNetwork(SimpleChannel channel)
    {
        this.channel = channel;
    }

    @Override
    public void sendToPlayer(Supplier<ServerPlayer> supplier, IMessage<?> message)
    {
        this.channel.send(PacketDistributor.PLAYER.with(supplier), message);
    }

    @Override
    public void sendToTracking(Supplier<Entity> supplier, IMessage<?> message)
    {
        this.channel.send(PacketDistributor.TRACKING_ENTITY.with(supplier), message);
    }

    @Override
    public void sendToServer(IMessage<?> message)
    {
        this.channel.sendToServer(message);
    }

    @Override
    public void sendToAll(IMessage<?> message)
    {
        this.channel.send(PacketDistributor.ALL.noArg(), message);
    }
}
