package com.mrcrayfish.framework.event.api;

import com.mrcrayfish.framework.api.event.IFrameworkEvent;
import net.minecraft.server.MinecraftServer;

/**
 * Author: MrCrayfish
 */
public interface ITickEvent extends IFrameworkEvent
{
    interface StartServer extends ITickEvent
    {
        void handle(MinecraftServer server);
    }

    interface EndServer extends ITickEvent
    {
        void handle(MinecraftServer server);
    }
}
