package test.syncedplayerdata;

import com.mrcrayfish.framework.api.data.Serializers;
import com.mrcrayfish.framework.api.data.SyncedDataKey;
import com.mrcrayfish.framework.common.data.SyncedPlayerData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Author: MrCrayfish
 */
@Mod("synced_player_data_test")
public class SyncedPlayerDataTest
{
    private BlockPos lastClickedPos = BlockPos.ZERO;

    private static final SyncedDataKey<Boolean> TOUCHED_GRASS = SyncedDataKey.builder(Serializers.BOOLEAN)
            .id(new ResourceLocation("synced_player_data_test", "touched_grass"))
            .defaultValueSupplier(() -> false)
            .resetOnDeath()
            .saveToFile()
            .build();

    public SyncedPlayerDataTest()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.addListener(this::onTouchBlock);
    }

    private void onTouchBlock(PlayerInteractEvent.LeftClickBlock event)
    {
        if(event.getSide() != LogicalSide.SERVER)
            return;

        if(!(event.getEntityLiving() instanceof Player player))
            return;

        if(this.lastClickedPos.equals(event.getPos()))
            return;

        BlockState state = player.level.getBlockState(event.getPos());
        if(state.getBlock() == Blocks.GRASS_BLOCK)
        {
            this.lastClickedPos = event.getPos();
            if(TOUCHED_GRASS.getValue(player))
            {
                player.displayClientMessage(new TextComponent("You've already touched grass!"), true);
            }
            else
            {
                TOUCHED_GRASS.setValue(player, true);
                player.displayClientMessage(new TextComponent("Well done, you've finally touched grass!"), true);
            }
        }
    }
}
