package com.mrcrayfish.framework.api;

import com.mrcrayfish.framework.FrameworkSetup;
import net.fabricmc.api.ModInitializer;

public abstract class FrameworkModInitializer implements ModInitializer
{
    static
    {
        FrameworkSetup.run();
    }
}
