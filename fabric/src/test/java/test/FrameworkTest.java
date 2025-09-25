package test;

import com.mrcrayfish.framework.FrameworkSetup;
import net.fabricmc.api.ModInitializer;

public class FrameworkTest implements ModInitializer
{
    public FrameworkTest()
    {
        FrameworkSetup.run();
    }

    @Override
    public void onInitialize()
    {

    }
}
