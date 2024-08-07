package test.syncedplayerdata;

import com.mrcrayfish.framework.api.FrameworkAPI;
import com.mrcrayfish.framework.api.sync.Serializers;
import com.mrcrayfish.framework.api.sync.SyncedClassKey;
import com.mrcrayfish.framework.api.sync.SyncedDataKey;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Author: MrCrayfish
 */
@Mod("synced_entity_data_test")
public class SyncedEntityDataTest
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

    public SyncedEntityDataTest()
    {
        MinecraftForge.EVENT_BUS.addListener(this::onTouchBlock);
        MinecraftForge.EVENT_BUS.addListener(this::onHitEntity);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onCommonSetup);
    }

    private void onCommonSetup(FMLCommonSetupEvent event)
    {
        event.enqueueWork(() -> {
            FrameworkAPI.registerSyncedDataKey(TOUCHED_GRASS);
            FrameworkAPI.registerSyncedDataKey(HIT_COUNT);
        });
    }

    private void onTouchBlock(PlayerInteractEvent.LeftClickBlock event)
    {
        if(event.getSide() != LogicalSide.SERVER)
            return;

        if(this.lastClickedPos.equals(event.getPos()))
            return;

        Player player = event.getEntity();
        BlockState state = player.level.getBlockState(event.getPos());
        if(state.getBlock() == Blocks.GRASS_BLOCK)
        {
            this.lastClickedPos = event.getPos();
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
    }

    private void onHitEntity(AttackEntityEvent event)
    {
        if(event.getTarget() instanceof Animal animal && !animal.getLevel().isClientSide())
        {
            int newCount = HIT_COUNT.getValue(animal) + 1;
            HIT_COUNT.setValue(animal, newCount);
            event.getEntity().displayClientMessage(Component.literal("This animal has been hit " + newCount + " times!"), true);
        }
    }
}
