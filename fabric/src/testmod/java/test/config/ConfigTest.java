package test.config;

import com.mrcrayfish.framework.api.config.BoolProperty;
import com.mrcrayfish.framework.api.config.ConfigProperty;
import com.mrcrayfish.framework.api.config.ConfigType;
import com.mrcrayfish.framework.api.config.EnumProperty;
import com.mrcrayfish.framework.api.config.FrameworkConfig;
import com.mrcrayfish.framework.api.config.IntProperty;
import net.minecraft.core.Direction;

/**
 * Author: MrCrayfish
 */
public class ConfigTest
{
    @FrameworkConfig(id = "framework_test", name = "my_test_config")
    public static final TestConfig TEST = new TestConfig();

    @FrameworkConfig(id = "framework_test", name = "my_test_sync_config", type = ConfigType.WORLD_SYNC)
    public static final TestConfig TEST_SYNC = new TestConfig();

    public static class TestConfig
    {
        @ConfigProperty(name = "canTouchGrass", comment = "Enables the ability to touch grass")
        public final BoolProperty canTouchGrass = BoolProperty.create(true);

        @ConfigProperty(name = "integerValue", comment = "A test integer with a valid range")
        public final IntProperty true_LULW = IntProperty.create(0, 0, 10);

        @ConfigProperty(name = "direction", comment = "A test enum property using direction")
        public final EnumProperty<Direction> direction = EnumProperty.create(Direction.NORTH);
    }
}
