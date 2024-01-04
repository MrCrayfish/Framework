package test.syncedentitydata;

import com.mrcrayfish.framework.api.FrameworkAPI;
import com.mrcrayfish.framework.api.sync.Serializers;
import com.mrcrayfish.framework.api.sync.SyncedClassKey;
import com.mrcrayfish.framework.api.sync.SyncedDataKey;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;

public class SyncedEntityDataTest implements ModInitializer
{
    private BlockPos lastClickedPos = BlockPos.ZERO;

    private static final SyncedDataKey<Player, Boolean> TOUCHED_GRASS = SyncedDataKey.builder(SyncedClassKey.PLAYER, Serializers.BOOLEAN)
            .id(new ResourceLocation("synced_entity_data_test", "touched_grass"))
            .defaultValueSupplier(() -> false)
            .resetOnDeath()
            .saveToFile()
            .syncMode(SyncedDataKey.SyncMode.SELF_ONLY)
            .build();

    private static final SyncedDataKey<Animal, Integer> HIT_COUNT = SyncedDataKey.builder(SyncedClassKey.ANIMAL, Serializers.INTEGER)
            .id(new ResourceLocation("synced_entity_data_test", "hit_count"))
            .defaultValueSupplier(() -> 0)
            .saveToFile()
            .syncMode(SyncedDataKey.SyncMode.TRACKING_ONLY)
            .build();

    @Override
    public void onInitialize()
    {
        FrameworkAPI.registerSyncedDataKey(TOUCHED_GRASS);
        FrameworkAPI.registerSyncedDataKey(HIT_COUNT);

        AttackEntityCallback.EVENT.register(this::onHitEntity);
        AttackBlockCallback.EVENT.register(this::onTouchBlock);
    }

    private InteractionResult onTouchBlock(Player player, Level level, InteractionHand hand, BlockPos pos, Direction direction)
    {
        if(level.isClientSide() || this.lastClickedPos.equals(pos))
            return InteractionResult.PASS;

        BlockState state = player.level().getBlockState(pos);
        if(state.getBlock() == Blocks.GRASS_BLOCK)
        {
            this.lastClickedPos = pos;
            if(TOUCHED_GRASS.getValue(player))
            {
                player.displayClientMessage(Component.literal("You've already touched grass!"), true);
            }
            else
            {
                TOUCHED_GRASS.setValue(player, true);
                player.displayClientMessage(Component.literal("Well done, you've finally touched grass!"), true);
            }
        }
        return InteractionResult.PASS;
    }

    private InteractionResult onHitEntity(Player player, Level level, InteractionHand hand, Entity entity, @Nullable EntityHitResult result)
    {
        if(entity instanceof Animal animal && !animal.level().isClientSide())
        {
            int newCount = HIT_COUNT.getValue(animal) + 1;
            HIT_COUNT.setValue(animal, newCount);
            player.displayClientMessage(Component.literal("This animal has been hit " + newCount + " times!"), true);
        }
        return InteractionResult.PASS;
    }
}
