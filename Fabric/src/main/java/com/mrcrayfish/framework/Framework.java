package com.mrcrayfish.framework;

import net.fabricmc.api.ModInitializer;

/**
 * Author: MrCrayfish
 */
public class Framework implements ModInitializer
{
    @Override
    public void onInitialize()
    {
        Setup.init();
    }
}
