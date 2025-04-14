package com.mrcrayfish.framework.client.model.geometry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mrcrayfish.framework.Constants;
import com.mrcrayfish.framework.api.serialize.DataObject;
import com.mrcrayfish.framework.client.model.IOpenModel;
import com.mrcrayfish.framework.client.model.OpenBlockModel;
import com.mrcrayfish.framework.util.Utils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.DelegateUnbakedModel;
import net.neoforged.neoforge.client.model.UnbakedModelLoader;
import org.jetbrains.annotations.Nullable;

/**
 * Author: MrCrayfish
 */
public class OpenModelGeometry extends DelegateUnbakedModel implements IOpenModel
{
    private final DataObject data;

    public OpenModelGeometry(OpenBlockModel model, @Nullable DataObject data)
    {
        super(model);
        this.data = data;
    }

    @Override
    public DataObject getData()
    {
        return this.data;
    }

    @EventBusSubscriber(modid = Constants.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class Loader implements UnbakedModelLoader<OpenModelGeometry>
    {
        @Override
        public OpenModelGeometry read(JsonObject object, JsonDeserializationContext context) throws JsonParseException
        {
            return new OpenModelGeometry(OpenBlockModel.Deserializer.INSTANCE.deserialize(object, OpenBlockModel.class, context), DataObject.convertNonNull(object.get("data")));
        }

        @SubscribeEvent
        public static void onModelRegister(ModelEvent.RegisterLoaders event)
        {
            event.register(Utils.rl("open_model"), new Loader());
        }
    }
}
