package test.login;

import com.mrcrayfish.framework.Constants;
import com.mrcrayfish.framework.api.FrameworkAPI;
import com.mrcrayfish.framework.api.data.login.ILoginData;
import net.fabricmc.api.ModInitializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.Optional;

public class LoginDataTest implements ModInitializer
{
    public static final Marker MARKER = MarkerFactory.getMarker("LOGIN_DATA_TEST");

    @Override
    public void onInitialize()
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
