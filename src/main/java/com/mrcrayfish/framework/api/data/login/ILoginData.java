package com.mrcrayfish.framework.api.data.login;

import com.mrcrayfish.framework.api.event.FrameworkEvent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * ILoginData is used to send custom data to clients during the handshake phase, i.e. when connecting
 * to a server (both integrated and dedicated). This is for syncing data required to join the server.
 * At any point during the readData phase, the client should be disconnected if unable to process the
 * data. Implementations must be registered using {@link FrameworkEvent.Register#registerLoginData(ResourceLocation, Supplier)}
 * for them to function.
 * <p>
 * Author: MrCrayfish
 */
public interface ILoginData
{
    /**
     * Writes custom data to the given buffer
     *
     * @param buffer a buffer instance
     */
    void writeData(FriendlyByteBuf buffer);

    /**
     * Reads and handles custom data from the buffer. Since the data sent is considered required to
     * play on the server, the returned optional must contain an error message if unable to process
     * the data. Returning a non-empty optional will stop the player from connecting to the server.
     * If the data is handled successfully, simply return an empty optional to prevent disconnecting.
     *
     * @param buffer the buffer to read from
     * @return an empty optional otherwise contains an error message if unable to process data
     */
    Optional<String> readData(FriendlyByteBuf buffer);
}
