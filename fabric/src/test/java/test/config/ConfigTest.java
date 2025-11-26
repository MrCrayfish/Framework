package test.config;

import com.mrcrayfish.framework.api.config.*;
import com.mrcrayfish.framework.api.config.validate.NumberRange;
import net.minecraft.core.Direction;

import java.util.List;

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

        @ConfigProperty(name = "subConfig", comment = "Sub config of more properties")
        public final SubConfig subConfig = new SubConfig();

        public static class SubConfig
        {
            @ConfigProperty(name = "myIntegerList", comment = "A list of integers")
            public final ListProperty<Integer> myList = ListProperty.create(ListProperty.INT, new NumberRange<>(6, 7), () -> List.of(6, 7));

            @ConfigProperty(name = "myStringList", comment = "A list of strings")
            public final ListProperty<String> stringList = ListProperty.create(ListProperty.STRING, () -> List.of("Hello", "World"));
        }
    }
}
