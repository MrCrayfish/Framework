package com.mrcrayfish.framework.event;

import com.mrcrayfish.framework.api.event.IFrameworkEvent;
import net.minecraft.server.MinecraftServer;

/**
 * Author: MrCrayfish
 */
public interface IServerEvent extends IFrameworkEvent
{
    interface Starting extends IServerEvent
    {
        void handle(MinecraftServer server);
    }

    interface Started extends IServerEvent
    {
        void handle(MinecraftServer server);
    }

    interface Stopping extends IServerEvent
    {
        void handle(MinecraftServer server);
    }

    interface Stopped extends IServerEvent
    {
        void handle(MinecraftServer server);
    }
}
