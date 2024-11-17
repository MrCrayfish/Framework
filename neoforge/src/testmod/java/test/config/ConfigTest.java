package test.config;

import com.mrcrayfish.framework.api.config.BoolProperty;
import com.mrcrayfish.framework.api.config.ConfigProperty;
import com.mrcrayfish.framework.api.config.ConfigType;
import com.mrcrayfish.framework.api.config.FrameworkConfig;

/**
 * Author: MrCrayfish
 */
public class ConfigTest
{
    @FrameworkConfig(id = "framework", name = "my_test_config")
    public static final TestConfig TEST = new TestConfig();

    @FrameworkConfig(id = "framework", name = "my_test_sync_config", type = ConfigType.WORLD_SYNC)
    public static final TestConfig TEST_SYNC = new TestConfig();

    public static class TestConfig
    {
        @ConfigProperty(name = "canTouchGrass", comment = "Enables the ability to touch grass")
        public final BoolProperty canTouchGrass = BoolProperty.create(true);
    }
}
