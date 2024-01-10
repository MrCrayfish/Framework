package test.login;

import com.mrcrayfish.framework.Constants;
import com.mrcrayfish.framework.api.FrameworkAPI;
import com.mrcrayfish.framework.api.data.login.ILoginData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.Optional;

/**
 * Author: MrCrayfish
 */
@Mod("login_data_test")
public class LoginDataTest
{
    public static final Marker MARKER = MarkerFactory.getMarker("LOGIN_DATA_TEST");

    public LoginDataTest(IEventBus bus)
    {
        FrameworkAPI.registerLoginData(new ResourceLocation("login_data_test", "test"), CustomData::new);
    }

    public static class CustomData implements ILoginData
    {
        @Override
        public void writeData(FriendlyByteBuf buffer)
        {
            buffer.writeUtf("Touch Grass");
        }

        @Override
        public Optional<String> readData(FriendlyByteBuf buffer)
        {
            String message = buffer.readUtf();
            Constants.LOG.debug(MARKER, "Received test login data: {}", message);
            return Optional.empty();
        }
    }
}
