package com.mrcrayfish.framework.event;

import com.mrcrayfish.framework.api.event.IFrameworkEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/**
 * Author: MrCrayfish
 */
public interface ITickEvent extends IFrameworkEvent
{
    interface StartClient extends ITickEvent
    {
        void handle();
    }

    interface EndClient extends ITickEvent
    {
        void handle();
    }

    interface StartServer extends ITickEvent
    {
        void handle(MinecraftServer server);
    }

    interface EndServer extends ITickEvent
    {
        void handle(MinecraftServer server);
    }

    interface StartPlayer extends ITickEvent
    {
        void handle(Player player);
    }

    interface EndPlayer extends ITickEvent
    {
        void handle(Player player);
    }

    interface StartLivingEntity extends ITickEvent
    {
        void handle(LivingEntity entity);
    }

    interface EndLivingEntity extends ITickEvent
    {
        void handle(LivingEntity entity);
    }
}
