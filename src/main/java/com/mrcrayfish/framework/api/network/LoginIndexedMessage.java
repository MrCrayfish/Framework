package com.mrcrayfish.framework.api.network;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.function.IntSupplier;

/**
 * Author: MrCrayfish
 */
public class LoginIndexedMessage implements IntSupplier
{
    public static final Marker HANDSHAKE = MarkerManager.getMarker("FRAMEWORK_HANDSHAKE");

    private int loginIndex;

    public void setLoginIndex(final int loginIndex)
    {
        this.loginIndex = loginIndex;
    }

    public int getLoginIndex()
    {
        return this.loginIndex;
    }

    @Override
    public int getAsInt()
    {
        return this.getLoginIndex();
    }
}
