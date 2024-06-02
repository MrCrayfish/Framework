package test.syncedplayerdata;

import com.mrcrayfish.framework.api.FrameworkAPI;
import com.mrcrayfish.framework.api.sync.DataSerializer;
import com.mrcrayfish.framework.api.sync.Serializers;
import com.mrcrayfish.framework.api.sync.SyncedClassKey;
import com.mrcrayfish.framework.api.sync.SyncedDataKey;
import com.mrcrayfish.framework.api.sync.SyncedObject;
import com.mrcrayfish.framework.network.FrameworkCodecs;
import com.mrcrayfish.framework.network.message.play.S2CUpdateEntityData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Zombie;
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

    private static final SyncedDataKey<Zombie, TestCounter> STRIKE_COUNT = SyncedDataKey.builder(SyncedClassKey.ZOMBIE, TestCounter.SERIALIZER)
        .id(new ResourceLocation("synced_entity_data_test", "strike_count"))
        .defaultValueSupplier(() -> new TestCounter(0))
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
        BlockState state = player.level().getBlockState(event.getPos());
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
        if(event.getTarget() instanceof Animal animal && !animal.level().isClientSide())
        {
            int newCount = HIT_COUNT.getValue(animal) + 1;
            HIT_COUNT.setValue(animal, newCount);
            event.getEntity().displayClientMessage(Component.literal("This animal has been hit " + newCount + " times!"), true);
        }

        if(event.getTarget() instanceof Zombie zombie && !zombie.level().isClientSide())
        {
            TestCounter counter = STRIKE_COUNT.getValue(zombie);
            counter.increment();
            event.getEntity().displayClientMessage(Component.literal("This zombie has been hit " + counter.getCount() + " times!"), true);
        }
    }

    private static class TestCounter extends SyncedObject
    {
        public static final StreamCodec<RegistryFriendlyByteBuf, TestCounter> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            TestCounter::getCount,
            TestCounter::new
        );
        public static final DataSerializer<TestCounter> SERIALIZER = new DataSerializer<>(STREAM_CODEC, TestCounter::write, TestCounter::read);

        private int count;

        public TestCounter(int count)
        {
            this.count = count;
        }

        public void increment()
        {
            this.count++;
            this.markDirty();
        }

        public int getCount()
        {
            return count;
        }

        private Tag write(HolderLookup.Provider provider)
        {
            return IntTag.valueOf(this.count);
        }

        private static TestCounter read(Tag tag, HolderLookup.Provider provider)
        {
            return new TestCounter(((IntTag) tag).getAsInt());
        }
    }
}
