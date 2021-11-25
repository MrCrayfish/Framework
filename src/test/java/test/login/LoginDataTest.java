package test.login;

import com.mrcrayfish.framework.Framework;
import com.mrcrayfish.framework.api.FrameworkAPI;
import com.mrcrayfish.framework.api.data.login.ILoginData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.Optional;

/**
 * Author: MrCrayfish
 */
@Mod("login_data_test")
public class LoginDataTest
{
    public static final Marker MARKER = MarkerManager.getMarker("LOGIN_DATA_TEST");

    public LoginDataTest()
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
            Framework.LOGGER.debug(MARKER, "Received test login data: {}", message);
            return Optional.empty();
        }
    }
}
