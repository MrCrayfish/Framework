package test.openmodeldata;

import com.mojang.serialization.MapCodec;
import com.mrcrayfish.framework.api.client.FrameworkClientAPI;
import com.mrcrayfish.framework.api.serialize.DataHelper;
import com.mrcrayfish.framework.api.serialize.DataObject;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Author: MrCrayfish
 */
public class TestTintSource implements ItemTintSource
{
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("open_model_data_test", "tint");
    public static final MapCodec<TestTintSource> MAP_CODEC = MapCodec.unit(new TestTintSource());

    @Override
    public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity)
    {
        DataObject object = FrameworkClientAPI.getOpenModelData(stack);
        return DataHelper.getIntOrDefault(object, "tint", -1);
    }

    @Override
    public MapCodec<TestTintSource> type()
    {
        return MAP_CODEC;
    }
}
